package it.epicode.flavor_hub.tag;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.List;

@Component
public class TagMapper {
    public TagResponse entityToDto(Tag entity) {
        if (entity == null) {
            return null;
        }
        TagResponse response = new TagResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    public List<TagResponse> entitiesToDtos(List<Tag> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.stream().map(this::entityToDto).toList();
    }

}
