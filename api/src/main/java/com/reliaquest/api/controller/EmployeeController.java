package com.reliaquest.api.controller;

import com.reliaquest.api.model.Response;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.service.MockEmployeeService;
import java.util.List;
import java.util.OptionalInt;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController {

    private final MockEmployeeService mockEmployeeService;

    @GetMapping()
    public Response<List<MockEmployee>> getAllEmployees() {
        return Response.handledWith(mockEmployeeService.getMockEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<MockEmployee>> getEmployeeById(@PathVariable("id") String uuid) {
        return mockEmployeeService
                .findById(uuid)
                .map(employee -> ResponseEntity.ok(Response.handledWith(employee)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.handled()));
    }

    @DeleteMapping()
    public Response<Boolean> deleteEmployee(@Valid @RequestBody DeleteMockEmployeeInput input) {
        return Response.handledWith(mockEmployeeService.delete(input));
    }

    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        OptionalInt maxSalary = mockEmployeeService.getHighestSalary();
        return maxSalary.isPresent()
                ? ResponseEntity.ok(maxSalary.getAsInt())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<Response<List<MockEmployee>>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return mockEmployeeService
                .findByName(searchString)
                .map(employee -> ResponseEntity.ok(Response.handledWith(employee)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.handled()));
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(mockEmployeeService.getTopTenHighestEarningEmployeeNameList());
    }

    @Override
    public Response createEmployee(CreateMockEmployeeInput employeeInput) {
        return  Response.handledWith(mockEmployeeService.create(employeeInput));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String uuid) {
        String employeeName = mockEmployeeService.deleteById(uuid);
        return employeeName != null
                ? ResponseEntity.ok(employeeName)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

