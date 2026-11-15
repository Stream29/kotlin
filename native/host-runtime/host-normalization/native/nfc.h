#ifndef NATIVEKOTLIN_NFC_H
#define NATIVEKOTLIN_NFC_H
#include <stdint.h>
#ifdef __cplusplus
extern "C" {
#endif
int32_t nativekotlin_nfc15(const uint16_t *source, int32_t length,
                          uint16_t *destination, int32_t capacity, int32_t *status);
#ifdef __cplusplus
}
#endif
#endif
