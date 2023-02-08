package com.common.utils.web.filter.trace;

import brave.Tracer;
import brave.propagation.TraceContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lyon
 * @since 1.0.1
 */
public class TraceFilter implements Filter {

    private Tracer tracer = null;

    public TraceFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain
    ) throws IOException, ServletException {

        /**
         * set trace context
         * notice: although we set the response http header, we should set it before we call the doFilter method
         */
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
        httpServletResponse.addHeader("x-sec-trace-id", traceId);
        httpServletResponse.addHeader("x-sec-span-id", spanId);

        //
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
