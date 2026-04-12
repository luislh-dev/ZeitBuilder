package com.zeitbuilder.zeitbuilder.testdata.example;

public class ManagerDTO extends EmployeeDTO {
    private int teamSize;
    private double budget;

    public ManagerDTO() {
    }

    protected ManagerDTO(Builder<?, ?> builder) {
        super(builder);
        this.teamSize = builder.teamSize;
        this.budget = builder.budget;
    }

    public static Builder<?, ?> builder() {
        return new ManagerDTOBuilderImpl();
    }

    public Builder<?, ?> toBuilder() {
        return new ManagerDTOBuilderImpl().teamSize(this.teamSize).budget(this.budget);
    }

    public static abstract class Builder<C extends ManagerDTO, B extends Builder<C, B>> extends EmployeeDTO.Builder<C, B> {
        private int teamSize;
        private double budget;

        public B teamSize(int teamSize) {
            this.teamSize = teamSize;
            return self();
        }

        public B budget(double budget) {
            this.budget = budget;
            return self();
        }

        protected abstract B self();

        public abstract C build();
    }

    private static final class ManagerDTOBuilderImpl extends Builder<ManagerDTO, ManagerDTOBuilderImpl> {
        @Override
        protected ManagerDTOBuilderImpl self() {
            return this;
        }

        @Override
        public ManagerDTO build() {
            return new ManagerDTO(this);
        }
    }
}

