#include "h2a_bigint.h"
#include "gmp.h"
#include <stdlib.h>
#include <string.h>
#include <limits.h>

char *h2a_bigint(int op, const char *a, const char *b, int shift, int radix, int *status) {
    mpz_t x, y, z;
    mpz_inits(x, y, z, NULL);
    char *out = NULL;
    *status = 0;
    if (mpz_set_str(x, a, op == 0 ? radix : 10) ||
        (b && mpz_set_str(y, b, 10))) {
        *status = 1;
        goto done;
    }
    switch (op) {
        case 0: mpz_set(z, x); break;
        case 1: mpz_add(z, x, y); break;
        case 2: mpz_sub(z, x, y); break;
        case 3: mpz_mul(z, x, y); break;
        case 4:
        case 5:
            if (mpz_sgn(y) == 0) { *status = 2; goto done; }
            if (op == 4) mpz_tdiv_q(z, x, y); else mpz_tdiv_r(z, x, y);
            break;
        case 6: mpz_and(z, x, y); break;
        case 7: mpz_ior(z, x, y); break;
        case 8: mpz_xor(z, x, y); break;
        case 9:
        case 10: {
            int64_t distance = (int64_t)shift;
            if (op == 10) distance = -distance;
            if (distance > 0 && mpz_sgn(x) != 0 &&
                (uint64_t)distance + mpz_sizeinbase(x, 2) > INT_MAX) {
                *status = 3; goto done;
            }
            if (distance >= 0) mpz_mul_2exp(z, x, (mp_bitcnt_t)distance);
            else mpz_fdiv_q_2exp(z, x, (mp_bitcnt_t)(-distance));
            break;
        }
        case 11: mpz_set_si(z, (mpz_cmp(x, y) > 0) - (mpz_cmp(x, y) < 0)); break;
        case 12: mpz_set(z, x); break;
        case 13: mpz_neg(z, x); break;
        default: *status = 4; goto done;
    }
    {
        int base = op == 12 ? radix : 10;
        size_t size = mpz_sizeinbase(z, base) + 3;
        out = malloc(size);
        if (!out) { *status = 5; goto done; }
        mpz_get_str(out, base, z);
    }
done:
    mpz_clears(x, y, z, NULL);
    return out;
}
void h2a_bigint_free(char *value) { free(value); }
int64_t h2a_bigint_long(const char *a) {
    mpz_t x;
    mpz_init(x);
    mpz_set_str(x, a, 10);
    mpz_fdiv_r_2exp(x, x, 64);
    uint64_t bits = 0;
    mpz_export(&bits, NULL, -1, sizeof(bits), 0, 0, x);
    mpz_clear(x);
    int64_t result;
    memcpy(&result, &bits, sizeof(result));
    return result;
}
double h2a_bigint_double(const char *a) { return strtod(a, NULL); }
float h2a_bigint_float(const char *a) { return strtof(a, NULL); }
