
#ifdef ANDROID
#define gli_strict_warning(msg) \
    (__android_log_print(ANDROID_LOG_WARN, "glk", "Library error: %s\n", msg))
#else
#define gli_strict_warning(msg) \
    (fprintf(stderr, "Glk library error: %s\n", msg))
#endif

typedef glui32 gli_case_block_t[2]; /* upper, lower */
/* If both are 0xFFFFFFFF, you have to look at the special-case table */

typedef glui32 gli_case_special_t[3]; /* upper, lower, title */
/* Each of these points to a subarray of the unigen_special_array
   (in cgunicode.c). In that subarray, element zero is the length,
   and that's followed by length unicode values. */
