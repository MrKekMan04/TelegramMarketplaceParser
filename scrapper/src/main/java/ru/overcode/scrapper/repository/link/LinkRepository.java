package ru.overcode.scrapper.repository.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.overcode.scrapper.model.link.Link;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
}
