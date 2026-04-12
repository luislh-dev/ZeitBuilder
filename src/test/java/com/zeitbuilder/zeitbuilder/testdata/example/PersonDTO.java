package com.zeitbuilder.zeitbuilder.testdata.example;

public class PersonDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;

    public PersonDTO() {
    }

    protected PersonDTO(Builder<?, ?> builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
    }

    public static Builder<?, ?> builder() {
        return new PersonDTOBuilderImpl();
    }

    public Builder<?, ?> toBuilder() {
        return new PersonDTOBuilderImpl().id(this.id).firstName(this.firstName).lastName(this.lastName).email(this.email);
    }

    public static abstract class Builder<C extends PersonDTO, B extends Builder<C, B>> {
        private String id;
        private String firstName;
        private String lastName;
        private String email;

        public B id(String id) {
            this.id = id;
            return self();
        }

        public B firstName(String firstName) {
            this.firstName = firstName;
            return self();
        }

        public B lastName(String lastName) {
            this.lastName = lastName;
            return self();
        }

        public B email(String email) {
            this.email = email;
            return self();
        }

        protected abstract B self();

        public abstract C build();
    }

    private static final class PersonDTOBuilderImpl extends Builder<PersonDTO, PersonDTOBuilderImpl> {
        @Override
        protected PersonDTOBuilderImpl self() {
            return this;
        }

        @Override
        public PersonDTO build() {
            return new PersonDTO(this);
        }
    }
}

