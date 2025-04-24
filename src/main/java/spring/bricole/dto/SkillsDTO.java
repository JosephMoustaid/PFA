package spring.bricole.dto;

import spring.bricole.common.Skill;
import java.util.List;

public record SkillsDTO(
        List<Skill> skills
) {
}
