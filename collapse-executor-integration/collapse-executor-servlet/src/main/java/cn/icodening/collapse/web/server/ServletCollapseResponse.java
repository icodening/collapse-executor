package cn.icodening.collapse.web.server;

import javax.servlet.http.HttpServletResponse;

/**
 * @author icodening
 * @date 2023.05.22
 */
public class ServletCollapseResponse {

    private final HttpServletResponse response;

    private final RecordableServletOutputStream recordableServletOutputStream;

    public ServletCollapseResponse(HttpServletResponse response, RecordableServletOutputStream recordableServletOutputStream) {
        this.response = response;
        this.recordableServletOutputStream = recordableServletOutputStream;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public RecordableServletOutputStream getRecordableServletOutputStream() {
        return recordableServletOutputStream;
    }
}
