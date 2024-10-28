package ru.overcode.gateway.repository.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.overcode.gateway.model.link.LinkOutbox;
import ru.overcode.shared.dto.event.ProcessType;

import java.util.List;

@Repository
public interface LinkOutboxRepository extends JpaRepository<LinkOutbox, Long> {

    List<LinkOutbox> findAllByProcessType(ProcessType processType);

    @Modifying
    @Query("""
            update LinkOutbox lo
            set lo.processType = :processType
            where lo.id = :id
            """)
    void setProcessType(Long id, ProcessType processType);
}
