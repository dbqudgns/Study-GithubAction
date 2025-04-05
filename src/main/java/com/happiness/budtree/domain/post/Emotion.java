package com.happiness.budtree.domain.post;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EmotionDeserializer.class)
public enum Emotion {
    EXCELLENT, //매우 좋음
    GOOD, //좋음
    SOSO, //그럭저럭
    BAD,  //나쁨
    TERRIBLE; //매우나쁨
}

