#ifndef H2A_BINARY_H
#define H2A_BINARY_H
#include <stdint.h>
#ifdef __cplusplus
extern "C" {
#endif
typedef struct {
    char digits[24];
    int32_t length;
    int32_t exponent;
    int32_t negative;
    int32_t exact;
    int32_t rounded_up;
    int32_t path;
} H2ABinaryDecimal;
int h2a_jdk_binary(double value, H2ABinaryDecimal *output);
#ifdef __cplusplus
}
#endif
#endif
