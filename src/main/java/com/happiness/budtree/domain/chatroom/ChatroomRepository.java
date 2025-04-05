package com.happiness.budtree.domain.chatroom;

import com.happiness.budtree.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("select c from Chatroom c where c.member = :member order by c.createdDate desc, c.roomId desc")
    List<Chatroom> getChatroomByMemberID(@Param("member") Member member);

}
