/*
 * Copyright (c) 1996, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/* BigInteger.hashCode, from the fixed JDK21 source, translated to unsigned
 * 32-bit C operations over GMP-exported magnitude words. See vendor/JDK-LICENSE.
 */
#include "h2a_bigint.h"
#include "gmp.h"
#include <stdlib.h>
#include <string.h>

int32_t h2a_bigint_hash(const char *a, int *status) {
    mpz_t x;
    mpz_init(x);
    mpz_set_str(x, a, 10);
    size_t capacity = (mpz_sizeinbase(x, 2) + 31) / 32;
    uint32_t *words = malloc(capacity * sizeof(uint32_t));
    uint32_t hash = 0;
    *status = 0;
    if (!words) { *status = 5; mpz_clear(x); return 0; }
    size_t count = 0;
    mpz_export(words, &count, 1, sizeof(uint32_t), 0, 0, x);
    for (size_t i = 0; i < count; ++i) hash = 31u * hash + words[i];
    if (mpz_sgn(x) < 0) hash = 0u - hash;
    free(words);
    mpz_clear(x);
    int32_t result;
    memcpy(&result, &hash, sizeof(result));
    return result;
}
