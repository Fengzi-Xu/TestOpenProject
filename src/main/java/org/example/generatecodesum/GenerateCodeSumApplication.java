package org.example.generatecodesum;

import dev.langchain4j.chain.Chain;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.listener.ChatModelRequest;
import dev.langchain4j.model.chat.listener.ChatModelResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;


@SpringBootApplication(exclude= {SecurityAutoConfiguration.class })
public class GenerateCodeSumApplication {

    public static void main(String[] args) {

        //String apiKey = System.getenv("OPENAI_API_KEY");
        String apiKey = "demo";

//        Langchainclient langchainclient="";
//        ModelResponse modelResponse="new()";
//        ModelRequest modelRequest="";


//        ChatModelResponse chatModelResponse=new ChatModelResponse();
//        ChatModelRequest chatModelRequest=new ChatModelRequest();

//        ChatLanguageModel model=Chat.builder()
//                .apiKey(apiKey)
//                .modelName(GPT_4_O_MINI)
//                .build();
        //OpenAiChatModel model = OpenAiChatModel;

        System.out.println(GPT_4_O_MINI);

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("demo").modelName(GPT_4_O_MINI)
                .build();
        String result = model.generate("Hello");
        System.out.println(result);




        String answer = model.generate("Say 'Hello World'");
        System.out.println(answer); // Hello World

        SpringApplication.run(GenerateCodeSumApplication.class, args);
    }

}
