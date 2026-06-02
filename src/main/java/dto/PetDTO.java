// dto/PetDTO.java
package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetDTO {
    private Long id;
    private CategoryDTO category;
    private String name;
    private List<String> photoUrls;
    private List<TagDTO> tags;
    private String status;

    public PetDTO() {}

    // Билдер (ручной)
    public static Builder builder() {
        return new Builder();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
    public List<TagDTO> getTags() { return tags; }
    public void setTags(List<TagDTO> tags) { this.tags = tags; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Внутренний класс Builder
    public static class Builder {
        private Long id;
        private CategoryDTO category;
        private String name;
        private List<String> photoUrls;
        private List<TagDTO> tags;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder category(CategoryDTO category) { this.category = category; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder photoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; return this; }
        public Builder tags(List<TagDTO> tags) { this.tags = tags; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public PetDTO build() {
            PetDTO pet = new PetDTO();
            pet.id = this.id;
            pet.category = this.category;
            pet.name = this.name;
            pet.photoUrls = this.photoUrls;
            pet.tags = this.tags;
            pet.status = this.status;
            return pet;
        }
    }
}