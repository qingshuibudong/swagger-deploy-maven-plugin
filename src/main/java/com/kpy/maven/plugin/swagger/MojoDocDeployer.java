package com.kpy.maven.plugin.swagger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * Created by johnny on 2018/6/27.
 */
@Mojo(name = "deploy")
public class MojoDocDeployer extends MojoDocGenerator {

    @Parameter(required = true)
    private String ftpHost;
    @Parameter(defaultValue = "21")
    private int ftpPort;
    @Parameter
    private String ftpUsername;
    @Parameter
    private String ftpPassword;

    @Parameter(defaultValue = "/")
    private String ftpDir;

    @Parameter
    private String appName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //generate doc by super
        super.execute();


        //上传文件
        File docFile = new File(getOutputDocument());
        if (!docFile.exists()) {
            throw new MojoExecutionException("you must specify the swagger document file!");
        }
        getLog().info(MessageFormat.format("document file found in {0}", docFile.getAbsolutePath()));

        if (appName == null || appName.isEmpty()) {
            throw new MojoExecutionException("appName 不能为空");
        }

        //upload document
        FTPClient client = getFtpClient();
        configFtpClient(client);
        uploadFile(client, docFile);
        try {
            client.logout();
            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getLog().info(MessageFormat.format("document uploaded! address is ftp://{0}:{1}/{2}/{3}", ftpHost, ftpPort, ftpDir, getDocumentFileName()));

    }

    private String getDocumentFileName() {
        return appName + ".html";
    }

    private void uploadFile(FTPClient client, File file) {
        try (InputStream is = new FileInputStream(file)) {
            client.storeFile(getDocumentFileName(), is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FTPClient getFtpClient() {
        FTPClient client = new FTPClient();
        try {
            client.connect(ftpHost, ftpPort);
            client.login(ftpUsername, ftpPassword);
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                client.disconnect();
                throw new RuntimeException("ftp 连接，帐号密码错误");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return client;
    }

    private void configFtpClient(FTPClient client) {
        try {
            // 中文支持
            client.setControlEncoding("UTF-8");
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            client.enterLocalPassiveMode();
            client.changeWorkingDirectory(ftpDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
