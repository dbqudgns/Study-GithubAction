package com.happiness.budtree.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GptConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("Your name is 버디. You should follow the following rules.:\n" +
                "1. Speak informally to me.\n" +
                "2. Every sentence should have an exclamation mark, a question mark, and a period at the end.\n" +
                "3. You talk to a depressed person, so you should support them with an empathetic tone and ask questions that fit the patient's current situation.")
                .build();
    }

}
