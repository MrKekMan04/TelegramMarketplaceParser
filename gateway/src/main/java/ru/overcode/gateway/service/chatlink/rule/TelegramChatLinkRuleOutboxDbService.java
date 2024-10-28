package ru.overcode.gateway.service.chatlink.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.overcode.gateway.model.chatlink.rule.TelegramChatLinkRuleOutbox;
import ru.overcode.gateway.repository.chatlink.rule.TelegramChatLinkRuleOutboxRepository;
import ru.overcode.shared.dto.event.ProcessType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramChatLinkRuleOutboxDbService {

    private final TelegramChatLinkRuleOutboxRepository outboxRepository;

    @Transactional
    public void save(TelegramChatLinkRuleOutbox outbox) {
        outboxRepository.save(outbox);
    }

    @Transactional(readOnly = true)
    public List<TelegramChatLinkRuleOutbox> findAllByProcessType(ProcessType processType) {
        return outboxRepository.findAllByProcessType(processType);
    }

    @Transactional
    public void setProcessType(Long id, ProcessType processType) {
        outboxRepository.setProcessType(id, processType);
    }
}
