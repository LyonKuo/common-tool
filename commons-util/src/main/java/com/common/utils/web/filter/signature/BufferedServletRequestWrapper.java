package com.common.utils.web.filter.signature;


import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BufferedServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] buffer;

    public BufferedServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        buffer = read(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new BufferedServletInputStream(buffer);
    }

    private byte[] read(InputStream is) throws IOException {

        byte[] buff = new byte[1024];

        int read;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            while ((read = is.read(buff)) > 0) {
                baos.write(buff, 0, read);
            }
            return baos.toByteArray();
        }
    }
}
