package com.darum.ems.employee.domain.Mappers.Impl.EmployeeMapper;

import com.darum.ems.employee.api.dto.EmployeeDetails;
import com.darum.ems.employee.domain.Employee;
import com.darum.ems.employee.domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class EmployeeMapperImpl implements Mapper<Employee, EmployeeDetails> {

    private final ModelMapper modelMapper;

    @Override
    public EmployeeDetails mapTo(Employee employee) {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setId(employee.getEmployeeId());
        employeeDetails.setFirstName(employee.getFirstName());
        employeeDetails.setLastName(employee.getLastName());
        employeeDetails.setEmail(employee.getEmail());
        employeeDetails.setDepartmentName(employee.getDepartment().getName());
        employeeDetails.setDepartmentCode(employee.getDepartment().getCode());
        employeeDetails.setStatus(employee.getStatus());

        return employeeDetails;
    }

    @Override
    public Employee mapFrom(EmployeeDetails employeeDetails) {
        return modelMapper.map(employeeDetails, Employee.class);
    }

    @Override
    public Iterable<EmployeeDetails> mapListTo(Iterable<Employee> employeeIterable) {
        return StreamSupport.stream(employeeIterable.spliterator(), false)
                .map(employee -> modelMapper.map(
                        employee, EmployeeDetails.class
                )).collect(Collectors.toList());
    }
}
