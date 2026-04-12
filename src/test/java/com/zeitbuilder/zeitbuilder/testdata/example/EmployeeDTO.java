package com.zeitbuilder.zeitbuilder.testdata.example;

public class EmployeeDTO extends PersonDTO {
    private String employeeId;
    private String department;
    private double salary;

    public EmployeeDTO() {
    }

    protected EmployeeDTO(Builder<?, ?> builder) {
        super(builder);
        this.employeeId = builder.employeeId;
        this.department = builder.department;
        this.salary = builder.salary;
    }

    public static Builder<?, ?> builder() {
        return new EmployeeDTOBuilderImpl();
    }

    public Builder<?, ?> toBuilder() {
        return new EmployeeDTOBuilderImpl().employeeId(this.employeeId).department(this.department).salary(this.salary);
    }

    public static abstract class Builder<C extends EmployeeDTO, B extends Builder<C, B>> extends PersonDTO.Builder<C, B> {
        private String employeeId;
        private String department;
        private double salary;

        public B employeeId(String employeeId) {
            this.employeeId = employeeId;
            return self();
        }

        public B department(String department) {
            this.department = department;
            return self();
        }

        public B salary(double salary) {
            this.salary = salary;
            return self();
        }

        protected abstract B self();

        public abstract C build();
    }

    private static final class EmployeeDTOBuilderImpl extends Builder<EmployeeDTO, EmployeeDTOBuilderImpl> {
        @Override
        protected EmployeeDTOBuilderImpl self() {
            return this;
        }

        @Override
        public EmployeeDTO build() {
            return new EmployeeDTO(this);
        }
    }
}

