package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.User;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.services.CommonService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements CommonService<UserRequestDto,Long>
{
    private final JwtUtil jwtUtil;
    private final DtoUtilities dtoUtilities;
    private final ExcelUtility excelUtility;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<?> create(UserRequestDto userRequestDto)
    {
        try {

            User user = userRepository.findByEmailOrMobile(userRequestDto.getEmail(), userRequestDto.getMobile());
            if (user != null) {
                logger.error("user already exist!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user already exist!");
            }
            user = dtoUtilities.userRequstDtoToUser(userRequestDto);
            user.setCreatedBy(userRequestDto.getName());
            user.setUpdatedBy(userRequestDto.getName());
            userRepository.save(user);
            logger.info("New user created successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("New user created successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while creating new user: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating new user: "+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> update(UserRequestDto userRequestDto, Long id) {
        try {

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("user not found with id: " + id));
            if (!user.isStatus()) {
                logger.info("user not found with id: "+id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found with id: " + id);
            }
            user = dtoUtilities.userRequstDtoToUser(userRequestDto);
            user.setId(id);
            user.setUpdatedBy(userRequestDto.getName());
            userRepository.save(user);
            logger.info("User updated successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("User updated successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while updating user: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating user: "+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("user not found with id: " + id));
            user.setStatus(false);
            userRepository.save(user);
            logger.info("user with id: "+id+" deleted successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("user with id: "+id+" deleted successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with id: "+id,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting user with id: "+id+", "+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> get(Long id) {
        try {
            User user = userRepository.findByIdAndStatusTrue(id);
            if (user == null) {
                logger.info("user not found with id: "+id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
            }
            UserResponseDto userResponseDto = dtoUtilities.userToUserResponseDto(user);
            logger.info("user with id: "+id+" found successfully!");
            return ResponseEntity.status(HttpStatus.FOUND).body(userResponseDto);
        } catch (Exception e) {
            logger.error("Error occurred while fetching user with id: "+id,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching user with id: "+id+", "+e.getMessage());
        }
    }
    public Object get(String email){
        User user = userRepository.findByEmail(email);
        System.out.println(user);
        return user;
    }
    @Override
    public ResponseEntity<?> getAll(Pageable pageable, String keyword) {
        try {
            List<User> userList = userRepository.findAll();
            System.out.println(userList);
            if (userList.isEmpty()) {
                logger.info("No users available!");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users available!");
            }
            List<UserResponseDto> userResponseDtoList = userList.stream()
                    .map(dtoUtilities::userToUserResponseDto).collect(Collectors.toList());
            logger.info("fetching all users successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(userResponseDtoList);
        } catch (Exception e) {
            logger.error("Error occurred while fetching user's all data",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching user's all data"+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> downloadDataAsExcel() {
        try {
            List<User> userList = userRepository.findAll();
            if (userList.isEmpty()) {
                logger.info("No users available!");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users available!");
            }
            List<UserResponseDto> userResponseDtoList = userList.stream().map(dtoUtilities::userToUserResponseDto)
                    .collect(Collectors.toList());
            String fileName = "user_excel_data.xlsx";
            String sheetName = fileName.substring(0,fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(userResponseDtoList, sheetName);
            logger.info("data as excel file downloaded successfully!");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error occurred while downloading data as excel",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while downloading data as excel: " + e.getMessage());
        }
    }
    @Override
    public ResponseEntity<?> downloadExcelSample()
    {
        try {
            String fileName = "user_excel_sample.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadExcelSample(UserRequestDto.class, sheetName);
            logger.info("Excel format download successfully!");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        }catch (Exception e)
        {
            logger.error("Error occurred while downloading excel format: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while downloading excel format");
        }
    }

    public ResponseEntity<?> uploadExcelFile(MultipartFile file,Class<?> currentClass)
    {
        try{
            if(excelUtility.isExcelFile(file))
            {
                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file,currentClass);
                validateDataResponse = validateDataFromDB(validateDataResponse);
                validateDataResponse.setTotalValidData(validateDataResponse.getValidDataList().size());
                validateDataResponse.setTotalInvalidData(validateDataResponse.getInvalidDataList().size());
                if(validateDataResponse.getTotalData()==0){
                    logger.info("No data available in excel sheet!");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data available in excel sheet!");
                }
                logger.info("Excel data validated successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
            }else {
                logger.info("Incorrect uploaded file type!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type! supported [.xlsx or xls] type");
            }
        }catch(Exception e)
        {
            logger.error("Error occurred while validating excel data",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while validating excel data: "+e.getMessage());
        }
    }
    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse){

        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
        for(int i=0;i<validList.size();)
        {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            UserRequestDto userRequestDto = (UserRequestDto) validatedData.getData();
            /*boolean flag = isExistInDB(userRequestDto.getDepartmentCode());
            if(!flag){
                ValidatedData invalidData = new ValidatedData();
                invalidData.setData(userRequestDto);
                invalidData.setMessage("department doesn't exist");
                invalidList.add(invalidData);
                validList.remove(validatedData);
                continue;
            }*/
            userRequestDtoList.add(userRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(userRequestDtoList);
        return validateDataResponse;
    }

//    public ResponseEntity<?> uploadExcelFile(MultipartFile file,Class<?> currentClass)
//    {
//        try{
//            if(excelUtility.isExcelFile(file))
//            {
//                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file,currentClass);
//                validateDataResponse = validateDataFromDB(validateDataResponse);
//                validateDataResponse.setTotalValidData(validateDataResponse.getValidDataList().size());
//                validateDataResponse.setTotalInvalidData(validateDataResponse.getInvalidDataList().size());
//                if(validateDataResponse.getTotalData()==0){
//                    logger.info("No data available in excel sheet!");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data available in excel sheet!");
//                }
//                logger.info("Excel data validated successfully!");
//                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
//            }else {
//                logger.info("Incorrect uploaded file type!");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type! supported [.xlsx or xls] type");
//            }
//        }catch(Exception e)
//        {
//            logger.error("Error occurred while validating excel data",e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while validating excel data: "+e.getMessage());
//        }
//    }
//    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse){
//
//        List<?> validList = validateDataResponse.getValidDataList();
//        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
//        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
//        for(int i=0;i<validList.size();)
//        {
//            ValidatedData validatedData = (ValidatedData) validList.get(i);
//            UserRequestDto userRequestDto = (UserRequestDto) validatedData.getData();
//            boolean flag = isExistInDB(userRequestDto.getDepartmentCode());
//            if(!flag){
//                ValidatedData invalidData = new ValidatedData();
//                invalidData.setData(userRequestDto);
//                invalidData.setMessage("department doesn't exist");
//                invalidList.add(invalidData);
//                validList.remove(validatedData);
//                continue;
//            }
//            userRequestDtoList.add(userRequestDto);
//            i++;
//        }
//        validateDataResponse.setValidDataList(userRequestDtoList);
//        return validateDataResponse;
//    }

    public boolean isExistInDB(Object keyword){
        String departmentCode = (String) keyword;
        Optional<Department> department = departmentRepository.findByDepartmentCode(departmentCode);
        return department.isPresent();
    }
    @Override
    public ResponseEntity<?> saveBulkData(List<UserRequestDto> userRequestDtoList) {
        try{
            String username = jwtUtil.getCurrentLoginUser();
            if(username == null) {
                logger.info("Unauthenticated user!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated user!");
            }
            List<User> userList = userRequestDtoList.stream()
                    .map((userRequestDto -> {
                        User user = dtoUtilities.userRequstDtoToUser(userRequestDto);
                        user.setCreatedBy(username);
                        user.setUpdatedBy(username);
                        return user;
                    }))
                    .collect(Collectors.toList());
            userRepository.saveAll(userList);
            logger.info("All data saved successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("All data saved successfully");
        }catch (Exception e){
            logger.error("Error occurred while saving bulk data, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: "+e.getMessage());
        }
    }
}
