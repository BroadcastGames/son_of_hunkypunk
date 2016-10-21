typedef struct textwin_data_struct textwin_data_t;

struct textwin_data_struct {
    glui32 curr_style;
    glui32 curr_linkval;
    int kb_request;
    int link_request;
    void *inbuf;
    int inbuf_unicode;
    int inbuf_len;
    jchar *outbuf;
    int outbuf_count;
    gidispatch_rock_t inbuf_rock;
};
