#ifndef NATIVEKOTLIN_C1I_HOST_H
#define NATIVEKOTLIN_C1I_HOST_H
#include <errno.h>
#include <fcntl.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

static inline void c1i_clear_errno(void) { errno = 0; }

/* Keep JVM deleteOnExit semantics without entering a destroyed Kotlin runtime. */
struct c1i_cleanup_entry { char *path; struct c1i_cleanup_entry *next; };
static struct c1i_cleanup_entry *c1i_cleanup_paths;
static pthread_mutex_t c1i_cleanup_mutex = PTHREAD_MUTEX_INITIALIZER;
static int c1i_cleanup_registered;
static void c1i_cleanup(void) {
    pthread_mutex_lock(&c1i_cleanup_mutex);
    while (c1i_cleanup_paths) {
        struct c1i_cleanup_entry *entry = c1i_cleanup_paths;
        c1i_cleanup_paths = entry->next;
        remove(entry->path);
        free(entry->path);
        free(entry);
    }
    pthread_mutex_unlock(&c1i_cleanup_mutex);
}
static inline int c1i_delete_on_exit(const char *path) {
    struct c1i_cleanup_entry *entry = malloc(sizeof(*entry));
    if (!entry) return ENOMEM;
    entry->path = strdup(path);
    if (!entry->path) { free(entry); return ENOMEM; }
    pthread_mutex_lock(&c1i_cleanup_mutex);
    if (!c1i_cleanup_registered) {
        if (atexit(c1i_cleanup)) {
            pthread_mutex_unlock(&c1i_cleanup_mutex);
            free(entry->path); free(entry); return ENOMEM;
        }
        c1i_cleanup_registered = 1;
    }
    entry->next = c1i_cleanup_paths;
    c1i_cleanup_paths = entry;
    pthread_mutex_unlock(&c1i_cleanup_mutex);
    return 0;
}
static inline int c1i_mkstemps(char *pattern, int suffix_length) {
    int fd = mkstemps(pattern, suffix_length);
    if (fd < 0) return -errno;
    if (close(fd)) return -errno;
    return 0;
}
static inline int c1i_is_symlink(const char *path) {
    struct stat st;
    return lstat(path, &st) == 0 && S_ISLNK(st.st_mode);
}

/* Only async-signal-safe libc calls run in the child, never Kotlin or a shell. */
static inline int c1i_spawn(char *const *argv, const char *cwd,
                            const char *input, const char *output,
                            const char *error_output, int merge_error) {
    int errors[2];
    if (pipe2(errors, O_CLOEXEC)) return -errno;
    pid_t pid = fork();
    if (pid < 0) {
        int e = errno; close(errors[0]); close(errors[1]); return -e;
    }
    if (pid == 0) {
        int e, fd;
        close(errors[0]);
        if (cwd && chdir(cwd)) goto failed;
        if (input) {
            fd = open(input, O_RDONLY);
            if (fd < 0) goto failed;
            if (dup2(fd, STDIN_FILENO) < 0) goto failed;
            if (fd != STDIN_FILENO) close(fd);
        }
        if (output) {
            fd = open(output, O_WRONLY | O_CREAT | O_TRUNC, 0600);
            if (fd < 0) goto failed;
            if (dup2(fd, STDOUT_FILENO) < 0) goto failed;
            if (fd != STDOUT_FILENO) close(fd);
        }
        if (merge_error) {
            if (dup2(STDOUT_FILENO, STDERR_FILENO) < 0) goto failed;
        } else if (error_output) {
            fd = open(error_output, O_WRONLY | O_CREAT | O_TRUNC, 0600);
            if (fd < 0) goto failed;
            if (dup2(fd, STDERR_FILENO) < 0) goto failed;
            if (fd != STDERR_FILENO) close(fd);
        }
        execvp(argv[0], argv);
failed:
        e = errno;
        while (write(errors[1], &e, sizeof(e)) < 0 && errno == EINTR) {}
        _exit(127);
    }
    close(errors[1]);
    int child_errno = 0, status;
    ssize_t n;
    do { n = read(errors[0], &child_errno, sizeof(child_errno)); } while (n < 0 && errno == EINTR);
    int read_errno = errno;
    close(errors[0]);
    while (waitpid(pid, &status, 0) < 0) { if (errno != EINTR) return -errno; }
    if (n < 0) return -read_errno;
    if (n > 0) return -child_errno;
    if (WIFEXITED(status)) return WEXITSTATUS(status);
    if (WIFSIGNALED(status)) return 128 + WTERMSIG(status);
    return -ECHILD;
}
#endif
