package com.happiness.budtree.domain.chatroom.DTO.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record ChatroomPartRQ(

        @NotNull(message = "몇번 항목인지 넣어주세요.")
        @Range(min = 1, max = 9, message = "항목 번호는 1번~9번까지 입니다.")
        Integer part,

        @NotNull(message = "항목에 대한 선택지기 몇번인지 넣어주세요.")
        @Range(min = 1, max = 4, message = "선택지는 1번~4번까지 입니다.")
        Integer choose
) {
}
