// Copyright (c) 2012 Integryst, LLC, http://www.integryst.com/
// See LICENSE.txt for licensing information

package com.integryst.kdbrowser;

import com.integryst.kdbrowser.httphelpers.HttpHelper;

import java.io.IOException;

import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class uploadaction extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    private static final String ACTION_PORTLET = "portlet";
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        String action = HttpHelper.getParameter(request, "action", ACTION_PORTLET);
        String responseText = "";
        
        if (ACTION_PORTLET.equals(action)) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/upload_form.jsp");
            dispatcher.forward(request, response);
            return;
        }

        PrintWriter out = response.getWriter();
        out.print(responseText);
    }
}
