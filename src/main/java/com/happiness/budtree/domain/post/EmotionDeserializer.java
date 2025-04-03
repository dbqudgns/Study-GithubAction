package com.happiness.budtree.domain.post;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;


public class EmotionDeserializer extends JsonDeserializer {
    public Emotion deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String text = jp.getText();
        for(Emotion emotion : Emotion.values()) {
            if(text.equals(emotion.toString())) {
                return emotion;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 감정입니다.");
    }
}
