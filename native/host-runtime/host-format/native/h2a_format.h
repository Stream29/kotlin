#ifndef H2A_FORMAT_H
#define H2A_FORMAT_H
#include <stdint.h>
#ifdef __cplusplus
extern "C" {
#endif
typedef struct {
    int32_t tag;
    const uint16_t *text;
    int32_t length;
    int64_t integer;
    double floating;
} H2AFormatArg;
enum { H2A_EMPTY_CHOICE_OFFSET = -10 };
enum {
    H2A_REQUEST_DEFAULT = 0,
    H2A_REQUEST_NUMBER = 1,
    H2A_REQUEST_DATE = 2,
    H2A_REQUEST_CHOICE = 3,
    H2A_HOST_FAILURE_OFFSET = -11
};
/* Output text is borrowed until the enclosing call returns. Zero means stop:
 * the host retains its Throwable and rethrows only after the C++ stack returns. */
typedef int32_t (*H2AFormatRequest)(void *context, int32_t index, int32_t request,
                                  H2AFormatArg *output);
uint16_t *h2a_format(const uint16_t *pattern, int32_t length, const char *locale,
                    const char *zone, H2AFormatRequest request, void *context, int32_t count,
                    const int8_t *decimal_digits,
                    int32_t *output_length, int32_t *status, int32_t *error_offset);
void h2a_format_free(uint16_t *text);
int h2a_format_error_kind(int status, int32_t offset);
#ifdef __cplusplus
}
#endif
#endif
