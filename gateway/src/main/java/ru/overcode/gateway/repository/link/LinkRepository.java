package ru.overcode.gateway.repository.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.link.Link;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByUrl(URI url);

    @Query("""
        select l from Link l
        join TelegramChatLink tcl on l.id = tcl.linkId
        where tcl.chatId = :chatId
        """)
    List<Link> findAllByChatId(Long chatId);
}
