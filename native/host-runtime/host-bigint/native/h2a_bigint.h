#ifndef H2A_BIGINT_H
#define H2A_BIGINT_H
#include <stdint.h>
char *h2a_bigint(int op, const char *a, const char *b, int shift, int radix, int *status);
void h2a_bigint_free(char *value);
int64_t h2a_bigint_long(const char *a);
double h2a_bigint_double(const char *a);
float h2a_bigint_float(const char *a);
int32_t h2a_bigint_hash(const char *a, int *status);
#endif
