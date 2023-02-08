package com.common.utils.web.filter.ip;

import com.common.utils.web.servlet.ServletUtils;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * deprecated，ip验证已经集成在f5check中
 * @deprecated
 * @author lyon
 * @since 1.0.1
 */
@Deprecated
public class IpFilter implements Filter {

    private Set<String> allowList;

    public IpFilter(Set<String> allowList) {
        this.allowList = allowList;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // if we don't config any ip address to the config file, it mean we reject all the request
        if(CollectionUtils.isEmpty(allowList)) {
            resp.setStatus(403);
            return;
        }

        // get the remote ip
        String ip = ServletUtils.getClientIP(req);

        if (!allowList.contains(ip)) {
            resp.setStatus(403);
            return;
        }

        //
        filter.doFilter(req, resp);
    }
}
