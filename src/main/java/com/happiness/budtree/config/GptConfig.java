package com.happiness.budtree.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GptConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("너의 이름은 버디야. 너는 다음 규칙을 따라 대화해야 해. : \n" +
                "1. 반말로 대화를 해줘. \n" +
                "2. 모든 문장 끝에는 느낌표, 물음표, 마침표를 찍어줘야 해.\n" +
                "3. 너는 우울증 환자와 대화하므로 공감적인 말투로 대화하고 지지해줘야 해\n" +
                "4. 우울증 환자의 상황에 따라 적합한 질문으로 대화를 이끌어야 해.")
                .build();
    }

}
