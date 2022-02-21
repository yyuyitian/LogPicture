/* Copyright � 2015 Oracle and/or its affiliates. All rights reserved. */
package com.example.employees;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.jni.OS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "FileUploadServlet",
        urlPatterns = {"/upload"}
)
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        if(!ServletFileUpload.isMultipartContent(request))
        {
            throw new RuntimeException("当前不支持文件上传");
        }
        try {
            DiskFileItemFactory factory=new DiskFileItemFactory();

            //设置使用临时文件的边界值，大鱼该值得文件上传会先保存在临时文件中，否则上传文件会直接写入到内存当中去
            //单位 字节 在这里设置的为1m
            factory.setSizeThreshold(1024*1024*1);
            //设置临时文件
            String temppath=this.getServletContext().getRealPath("/temp");
            File file2=new File(temppath);
            factory.setRepository(file2);
            //创建文件上传核心组件
            ServletFileUpload fileUpload=new ServletFileUpload(factory);
            //设置单个文件的大小不超过多少
            fileUpload.setFileSizeMax(1024*1024*2);
            //上传多个文件的要求总的大小不超过多少
            fileUpload.setSizeMax(1024*1024*5);
            fileUpload.setHeaderEncoding("utf-8");
            //设置每一个item的字符编码为utf-8

            List<FileItem> items=fileUpload.parseRequest(request);
            for (FileItem fileItem : items) {
                if(fileItem.isFormField())
                {
                    String filename=fileItem.getFieldName();
                    String fileValue=fileItem.getString("utf-8");
                    System.out.println(filename+"----"+fileValue);

                }
                else //如果是文件上传表单项
                {
                    String filename=fileItem.getName();
                    InputStream inputStream=fileItem.getInputStream();
                    //获取表单项的输入流
                    String path=this.getServletContext().getRealPath("/fileContents");
                    File file=new File(path,filename);
                    OutputStream outputStream=new FileOutputStream(file);
                    int len=-1;
                    byte[] bs=new byte[1024];
                    while((len=inputStream.read(bs))!=-1)
                    {

                        outputStream.write(bs,0,len);


                    }
                    outputStream.close();
                    inputStream.close();
                    //删除临时文件
                    fileItem.delete();
                }
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }
    }
}
