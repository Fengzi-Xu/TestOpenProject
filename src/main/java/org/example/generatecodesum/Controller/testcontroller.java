package org.example.generatecodesum.Controller;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

//import static dev.langchain4j.model.openai.OpenAiChatModelName.Qwen;
//import static dev.langchain4j.model.dashscope.QwenChatModel;
@RequestMapping("/chat/")
@RestController
public class testcontroller {

    private ChatLanguageModel chatLanguageModel;


    @Autowired
    private ProjectService projectService;


    public void ChatController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @GetMapping("/chat/{prompt}")
    public String chat(@PathVariable String prompt) {
        // 创建 Ollama 聊天模型实例
        chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434") // Ollama 服务的 URL
                .modelName("qwen") // 使用的模型名称
                .build();
        return chatLanguageModel.generate(prompt);
    }
    @GetMapping("/test/{prompt}")
    public String test(@PathVariable String prompt) {
        return prompt;
    }

    @GetMapping("/test/dashscope/{prompt}")//ok
    public String test2(@PathVariable String prompt) {
        chatLanguageModel=QwenChatModel.builder().apiKey("sk-f33956972ebe473dbb271200d2a108e6").modelName("qwen-long").build();
        return chatLanguageModel.generate(prompt);
    }


    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String path) throws IOException {
        byte[] fileData = projectService.downloadFile(owner, repo, path, "downloaded_" + path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path)
                .body(new InputStreamResource(new ByteArrayInputStream(fileData)));
    }

    @GetMapping("/download-all")
    public ResponseEntity<InputStreamResource> downloadAllFiles(
            @RequestParam String owner,
            @RequestParam String repo) throws Exception {
        byte[] zipData = projectService.downloadAllFilesAsZip(owner, repo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + repo + ".zip")
                .body(new InputStreamResource(new ByteArrayInputStream(zipData)));
    }




}