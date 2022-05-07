package me.zyy.reggie.controller;

import me.zyy.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file是一个临时文件，需要指定到某个位置存储，请求结束后会自动删除

        String originalFilename = file.getOriginalFilename(); // xxx.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")); // suffix = .jpg

        // 生成文件名
        String filename = UUID.randomUUID().toString() + suffix;

        // 在路径不存在时新建目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(1, filename, "上传成功");
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        // 先通过输入流 读取name对应的文件内容
        FileInputStream fis = null;
        // 再通过输出流文件传输到浏览器
        ServletOutputStream ops = null;

        // 设置响应格式
        response.setContentType("image/jpeg");

        try {
            fis = new FileInputStream(new File(basePath + name));
            ops = response.getOutputStream();
            int len;
            byte[] b = new byte[1024];
            while ((len = fis.read(b)) != -1) {
                ops.write(b, 0, len);
                ops.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ops != null) {
                    ops.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
