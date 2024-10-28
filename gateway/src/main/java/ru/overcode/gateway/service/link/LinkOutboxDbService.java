package ru.overcode.gateway.service.link;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.link.LinkOutbox;
import ru.overcode.gateway.repository.link.LinkOutboxRepository;
import ru.overcode.shared.dto.event.ProcessType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkOutboxDbService {

    private final LinkOutboxRepository linkOutboxRepository;

    @Transactional
    public void save(LinkOutbox linkOutbox) {
        linkOutboxRepository.save(linkOutbox);
    }

    @Transactional(readOnly = true)
    public List<LinkOutbox> findAllByProcessType(ProcessType processType) {
        return linkOutboxRepository.findAllByProcessType(processType);
    }

    @Transactional
    public void setProcessType(Long id, ProcessType processType) {
        linkOutboxRepository.setProcessType(id, processType);
    }
}
