package com.zeitbuilder.zeitbuilder.testdata.example;

public class DeveloperDTO extends EmployeeDTO {
    private String programmingLanguage;
    private String githubProfile;

    public DeveloperDTO() {
    }

    protected DeveloperDTO(Builder<?, ?> builder) {
        super(builder);
        this.programmingLanguage = builder.programmingLanguage;
        this.githubProfile = builder.githubProfile;
    }

    public static Builder<?, ?> builder() {
        return new DeveloperDTOBuilderImpl();
    }

    public Builder<?, ?> toBuilder() {
        return new DeveloperDTOBuilderImpl().programmingLanguage(this.programmingLanguage).githubProfile(this.githubProfile);
    }

    public static abstract class Builder<C extends DeveloperDTO, B extends Builder<C, B>> extends EmployeeDTO.Builder<C, B> {
        private String programmingLanguage;
        private String githubProfile;

        public B programmingLanguage(String programmingLanguage) {
            this.programmingLanguage = programmingLanguage;
            return self();
        }

        public B githubProfile(String githubProfile) {
            this.githubProfile = githubProfile;
            return self();
        }

        protected abstract B self();

        public abstract C build();
    }

    private static final class DeveloperDTOBuilderImpl extends Builder<DeveloperDTO, DeveloperDTOBuilderImpl> {
        @Override
        protected DeveloperDTOBuilderImpl self() {
            return this;
        }

        @Override
        public DeveloperDTO build() {
            return new DeveloperDTO(this);
        }
    }
}

