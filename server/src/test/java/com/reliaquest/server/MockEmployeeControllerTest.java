package com.reliaquest.server;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.reliaquest.server.controller.MockEmployeeController;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.service.MockEmployeeService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class MockEmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MockEmployeeService mockEmployeeService;

    @InjectMocks
    private MockEmployeeController employeeController;

    private MockEmployee mockEmployee;

    private UUID employeeId;

    private CreateMockEmployeeInput input;

    private DeleteMockEmployeeInput deleteInput;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        mockEmployee = MockEmployee.from("reliaQuest@gmail.com", createMockEmployeeInput());
        employeeId = UUID.randomUUID();
        input = createMockEmployeeInput();
        deleteInput = new DeleteMockEmployeeInput();
    }

    @Test
    public void testGetEmployeesList() throws Exception {
        List<MockEmployee> employees = Collections.singletonList(mockEmployee);
        when(mockEmployeeService.getMockEmployees()).thenReturn(employees);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(mockEmployeeService, times(1)).getMockEmployees();
    }

    @Test
    public void testGetEmployeeById() throws Exception {
        when(mockEmployeeService.findById(employeeId)).thenReturn(Optional.of(mockEmployee));

        mockMvc.perform(get("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(mockEmployeeService, times(1)).findById(employeeId);
    }

    @Test
    public void testCreateEmployee() throws Exception {
        when(mockEmployeeService.create(input)).thenReturn(mockEmployee);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType("application/json")
                        .content("{\"name\":\"name1\",\"age\":25,\"salary\":421520,\"title\":\"title1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(mockEmployeeService, times(1)).create(input);
    }

    @Test
    public void testGetHighestSalaryOfEmployees_Success() throws Exception {
        OptionalInt maxSalary = OptionalInt.of(100000);

        when(mockEmployeeService.getHighestSalary()).thenReturn(maxSalary);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("100000"));

        verify(mockEmployeeService, times(1)).getHighestSalary();
    }

    @Test
    public void testGetHighestSalaryOfEmployees_NotFound() throws Exception {
        OptionalInt maxSalary = OptionalInt.empty();
        when(mockEmployeeService.getHighestSalary()).thenReturn(maxSalary);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/highestSalary"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
        verify(mockEmployeeService, times(1)).getHighestSalary();
    }

    @Test
    public void testGetEmployeesByNameSearch() throws Exception {
        List<MockEmployee> employees = Collections.singletonList(mockEmployee);
        when(mockEmployeeService.findByName("name1")).thenReturn(Optional.of(employees));

        mockMvc.perform(get("/api/v1/employee/search/{searchString}", "name1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(mockEmployeeService, times(1)).findByName("name1");
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> topEmployeeNames = Arrays.asList(
                "name1", "name2", "name3", "name4", "name5", "name6", "name7", "name8", "name9", "name10");
        when(mockEmployeeService.getTopTenHighestEarningEmployeeNameList()).thenReturn(topEmployeeNames);
        String expected =
                "[\"name1\",\"name2\",\"name3\",\"name4\",\"name5\",\"name6\",\"name7\",\"name8\",\"name9\",\"name10\"]";
        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));

        verify(mockEmployeeService, times(1)).getTopTenHighestEarningEmployeeNameList();
    }

    @Test
    public void testDeleteEmployeeById() throws Exception {
        when(mockEmployeeService.deleteById(employeeId)).thenReturn("name1");

        mockMvc.perform(delete("/api/v1/employee/{id}", employeeId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("name1"));

        verify(mockEmployeeService, times(1)).deleteById(employeeId);
    }

    public CreateMockEmployeeInput createMockEmployeeInput() {
        CreateMockEmployeeInput createMockEmployeeInput = new CreateMockEmployeeInput();
        createMockEmployeeInput.setAge(25);
        createMockEmployeeInput.setName("name1");
        createMockEmployeeInput.setSalary(421520);
        createMockEmployeeInput.setTitle("title1");
        return createMockEmployeeInput;
    }
}
