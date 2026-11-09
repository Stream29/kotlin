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
#include <unistd.h>
#ifdef _WIN32
#include <windows.h>
#include <io.h>
#include <direct.h>
#else
#include <sys/wait.h>
#endif

static inline void c1i_clear_errno(void) { errno = 0; }

struct c1i_cleanup_entry { char *path; struct c1i_cleanup_entry *next; };
static struct c1i_cleanup_entry *c1i_cleanup_paths;
static pthread_mutex_t c1i_cleanup_mutex = PTHREAD_MUTEX_INITIALIZER;
static int c1i_cleanup_registered;
static void c1i_cleanup(void) {
    pthread_mutex_lock(&c1i_cleanup_mutex);
    while (c1i_cleanup_paths) {
        struct c1i_cleanup_entry *entry = c1i_cleanup_paths;
        c1i_cleanup_paths = entry->next;
        if (remove(entry->path) != 0) rmdir(entry->path);
        free(entry->path);
        free(entry);
    }
    pthread_mutex_unlock(&c1i_cleanup_mutex);
}
static inline int c1i_delete_on_exit(const char *path) {
    struct c1i_cleanup_entry *entry = (struct c1i_cleanup_entry *)malloc(sizeof(*entry));
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

#ifdef _WIN32
static inline void c1i_fill_random_x(char *x_pos, unsigned int attempt) {
    static const char hex[] = "0123456789abcdef";
    unsigned int v = (unsigned int)GetCurrentProcessId() ^ ((unsigned int)GetTickCount() * 1664525u) ^ (attempt * 1013904223u);
    for (int i = 0; i < 6; ++i) {
        x_pos[i] = hex[(v >> (i * 4)) & 0xf];
    }
}

static inline int c1i_mkstemps(char *pattern, int suffix_length) {
    int len = (int)strlen(pattern);
    if (len < suffix_length + 6) return -EINVAL;
    char *x_pos = pattern + len - suffix_length - 6;
    for (unsigned int attempt = 0; attempt < 1000; ++attempt) {
        c1i_fill_random_x(x_pos, attempt);
        int fd = _open(pattern, _O_CREAT | _O_EXCL | _O_RDWR | _O_BINARY, _S_IREAD | _S_IWRITE);
        if (fd >= 0) {
            _close(fd);
            return 0;
        }
        if (errno != EEXIST) return -errno;
    }
    return -EEXIST;
}

static inline char *c1i_mkdtemp(char *pattern) {
    int len = (int)strlen(pattern);
    if (len < 6) { errno = EINVAL; return NULL; }
    char *x_pos = pattern + len - 6;
    for (unsigned int attempt = 0; attempt < 1000; ++attempt) {
        c1i_fill_random_x(x_pos, attempt);
        if (_mkdir(pattern) == 0) return pattern;
        if (errno != EEXIST) return NULL;
    }
    errno = EEXIST;
    return NULL;
}

static inline int c1i_is_symlink(const char *path) {
    (void)path;
    return 0;
}

static inline void c1i_append_quoted_arg(char **buf, size_t *len, size_t *cap, const char *arg, int is_prog) {
    size_t arg_len = strlen(arg);
    size_t needed = *len + arg_len * 2 + 8;
    if (needed > *cap) {
        *cap = needed * 2;
        *buf = (char *)realloc(*buf, *cap);
    }
    if (*len > 0) (*buf)[(*len)++] = ' ';
    int needs_quotes = is_prog || arg_len == 0;
    for (size_t i = 0; i < arg_len; ++i) {
        if (arg[i] == ' ' || arg[i] == '\t' || arg[i] == '"') {
            needs_quotes = 1;
            break;
        }
    }
    if (!needs_quotes) {
        memcpy(*buf + *len, arg, arg_len);
        *len += arg_len;
        (*buf)[*len] = '\0';
        return;
    }
    (*buf)[(*len)++] = '"';
    size_t bs_count = 0;
    for (size_t i = 0; i < arg_len; ++i) {
        char c = arg[i];
        if (is_prog && c == '/') c = '\\';
        if (c == '\\') {
            bs_count++;
        } else if (c == '"') {
            for (size_t k = 0; k < bs_count * 2 + 1; ++k) (*buf)[(*len)++] = '\\';
            (*buf)[(*len)++] = '"';
            bs_count = 0;
        } else {
            for (size_t k = 0; k < bs_count; ++k) (*buf)[(*len)++] = '\\';
            bs_count = 0;
            (*buf)[(*len)++] = c;
        }
    }
    for (size_t k = 0; k < bs_count * 2; ++k) (*buf)[(*len)++] = '\\';
    (*buf)[(*len)++] = '"';
    (*buf)[*len] = '\0';
}

static inline int c1i_spawn(char *const *argv, const char *cwd,
                            const char *input, const char *output,
                            const char *error_output, int merge_error) {
    size_t len = 0, cap = 1024;
    char *cmdline = (char *)malloc(cap);
    if (!cmdline) return -ENOMEM;
    cmdline[0] = '\0';
    char *prog_exe = NULL;
    if (argv[0] != NULL && _access(argv[0], 0) != 0) {
        size_t plen = strlen(argv[0]);
        prog_exe = (char *)malloc(plen + 5);
        if (prog_exe) {
            memcpy(prog_exe, argv[0], plen);
            memcpy(prog_exe + plen, ".exe", 5);
            if (_access(prog_exe, 0) != 0) {
                free(prog_exe);
                prog_exe = NULL;
            }
        }
    }
    for (int i = 0; argv[i] != NULL; ++i) {
        const char *arg = (i == 0 && prog_exe != NULL) ? prog_exe : argv[i];
        c1i_append_quoted_arg(&cmdline, &len, &cap, arg, i == 0);
    }
    if (prog_exe) free(prog_exe);

    SECURITY_ATTRIBUTES sa;
    memset(&sa, 0, sizeof(sa));
    sa.nLength = sizeof(sa);
    sa.bInheritHandle = TRUE;

    HANDLE hIn = INVALID_HANDLE_VALUE;
    HANDLE hOut = INVALID_HANDLE_VALUE;
    HANDLE hErr = INVALID_HANDLE_VALUE;

    STARTUPINFOA si;
    memset(&si, 0, sizeof(si));
    si.cb = sizeof(si);
    si.dwFlags = STARTF_USESTDHANDLES;
    si.hStdInput = GetStdHandle(STD_INPUT_HANDLE);
    si.hStdOutput = GetStdHandle(STD_OUTPUT_HANDLE);
    si.hStdError = GetStdHandle(STD_ERROR_HANDLE);

    if (input) {
        hIn = CreateFileA(input, GENERIC_READ, FILE_SHARE_READ | FILE_SHARE_WRITE, &sa, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
        if (hIn == INVALID_HANDLE_VALUE) { free(cmdline); return -ENOENT; }
        si.hStdInput = hIn;
    }
    if (output) {
        hOut = CreateFileA(output, GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, &sa, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
        if (hOut == INVALID_HANDLE_VALUE) {
            if (hIn != INVALID_HANDLE_VALUE) CloseHandle(hIn);
            free(cmdline);
            return -EIO;
        }
        si.hStdOutput = hOut;
    }
    if (merge_error) {
        si.hStdError = si.hStdOutput;
    } else if (error_output) {
        hErr = CreateFileA(error_output, GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, &sa, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
        if (hErr == INVALID_HANDLE_VALUE) {
            if (hIn != INVALID_HANDLE_VALUE) CloseHandle(hIn);
            if (hOut != INVALID_HANDLE_VALUE) CloseHandle(hOut);
            free(cmdline);
            return -EIO;
        }
        si.hStdError = hErr;
    }

    PROCESS_INFORMATION pi;
    memset(&pi, 0, sizeof(pi));
    BOOL ok = CreateProcessA(
        NULL,
        cmdline,
        NULL,
        NULL,
        TRUE,
        0,
        NULL,
        cwd,
        &si,
        &pi
    );
    free(cmdline);
    if (hIn != INVALID_HANDLE_VALUE) CloseHandle(hIn);
    if (hOut != INVALID_HANDLE_VALUE) CloseHandle(hOut);
    if (hErr != INVALID_HANDLE_VALUE) CloseHandle(hErr);

    if (!ok) {
        return -ENOENT;
    }
    WaitForSingleObject(pi.hProcess, INFINITE);
    DWORD exit_code = 0;
    GetExitCodeProcess(pi.hProcess, &exit_code);
    CloseHandle(pi.hThread);
    CloseHandle(pi.hProcess);
    return (int)exit_code;
}
#else
static inline int c1i_mkstemps(char *pattern, int suffix_length) {
    int fd = mkstemps(pattern, suffix_length);
    if (fd < 0) return -errno;
    if (close(fd)) return -errno;
    return 0;
}
static inline char *c1i_mkdtemp(char *pattern) {
    return mkdtemp(pattern);
}
static inline int c1i_is_symlink(const char *path) {
    struct stat st;
    return lstat(path, &st) == 0 && S_ISLNK(st.st_mode);
}
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
#endif
