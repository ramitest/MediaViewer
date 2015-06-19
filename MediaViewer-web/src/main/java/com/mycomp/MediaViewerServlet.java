package com.mycomp;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import java.util.*;
import org.json.simple.JSONArray;

public class MediaViewerServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String pathInfo = request.getPathInfo();
            System.out.println(request.getRequestURL());
            System.out.println(pathInfo);
            System.out.println("Host and Port:: " + request.getServerName()+":"+request.getServerPort());
            
            switch ( pathInfo.toLowerCase() ) {
                case "/getimagedirs":
                    out.println( new ImageReader(getMedRootDir(), getMedRootDir(request)).getSubPathsOfPicsDirs() );
                    break;
                case "/getdirimages":
                    String dir = request.getParameter("directory");
                    out.println(new ImageReader(getMedRootDir(), getMedRootDir(request)).getPicsFromDir(dir, false));
                    break;
                case "/getvideodirs":
                    out.println(new VideoReader(getMedRootDir(), getMedRootDir(request)).getSubPathsOfVidsDirs() );
                    break;
                case "/getdirvideos":
                    dir = request.getParameter("directory");
                    out.println(new VideoReader(getMedRootDir(), getMedRootDir(request)).getVidsFromDir(dir, false));
                    break;
            }
        }
    }
    
    protected String getMedRootDir() {
        String mediaDir = System.getProperty("my.imgviewer.imgdir");
        System.out.println("sys prop media dir:: " + mediaDir );
        if(mediaDir == null) {
            mediaDir = getServletConfig().getInitParameter("my.imgviewer.imgdir");
            System.out.println("init param media dir:: " + mediaDir );
        }
        return mediaDir;
    }

    protected String getMedRootDir(HttpServletRequest request) {
        return "http://"+ request.getServerName()+":"+request.getServerPort()+"/imgviewer/";
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
