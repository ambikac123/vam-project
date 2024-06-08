package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.UserTypeRequestDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.UserTypeResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import com.dreamsol.entites.UserType;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UserTypeRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.services.CommonService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTypeServiceImpl implements CommonService<UserTypeRequestDto,Long>
{
    @PersistenceContext
    private EntityManager entityManager;
    private final DtoUtilities dtoUtilities;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;
    private final UserTypeRepository userTypeRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserTypeServiceImpl.class);
    @Override
    public ResponseEntity<?> create(UserTypeRequestDto userTypeRequestDto) {
        try {

            Optional<UserType> userTypeOptional = userTypeRepository.findByUserTypeNameOrUserTypeCode(userTypeRequestDto.getUserTypeName(),userTypeRequestDto.getUserTypeCode());
            if(userTypeOptional.isPresent()){
                userTypeOptional.get().setStatus(userTypeRequestDto.isStatus());
                userTypeRepository.save(userTypeOptional.get());
                logger.info("user type already exist! [usertype reactivated]");
                return ResponseEntity.status(HttpStatus.FOUND).body("user type already exist! [usertype reactivated]");
            }

            UserType userType = dtoUtilities.userTypeRequestDtoToUserType(userTypeRequestDto);
            userTypeRepository.save(userType);

            logger.info("New user type created successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("New user type created successfully!");
        }catch (Exception e){
            logger.error("Error occurred while creating user type, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating user type: "+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> update(UserTypeRequestDto userTypeRequestDto, Long id) {
        try {
            UserType userType = userTypeRepository.findByIdAndStatusTrue(id).orElseThrow(() -> new ResourceNotFoundException("usertype", "id", id));
            BeanUtils.copyProperties(userTypeRequestDto, userType);
            userType.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            userTypeRepository.save(userType);
            logger.info("user type updated successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("user type updated successfully!");
        }catch (Exception e){
            logger.error("Error occurred while updating user type, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating user type: "+e.getMessage());
        }
    }
    @Override
    public ResponseEntity<?> delete(Long id) {
        try{
            UserType userType = userTypeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("usertype", "id", id));
            userType.setStatus(false);
            userTypeRepository.save(userType);
            logger.info("usertype with id:"+id+" deleted successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("usertype with id:"+id+" deleted successfully!");
        }catch(Exception e){
            logger.error("Error occurred while deleting usertype: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting usertype: "+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> get(Long id) {
        try{
            UserType userType = userTypeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("usertype", "id", id));
            logger.info("usertype with id: " + id + " found!");
            return ResponseEntity.status(HttpStatus.FOUND).body(userType);

        }catch(Exception e){
            logger.error("Error occurred while searching usertype with id: "+id+" ,",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while searching usertype with id: "+id+" ,"+e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getAll(Integer pageNumber,Integer pageSize,String sortBy,String sortDir,Long unitId,Boolean status,String search)
    {
        Sort sort = sortDir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, sort);
        List<UserType> userTypeList = userTypeRepository.findByFilters(unitId,status,search,search,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userTypeList);
    }

    @Override
    public ResponseEntity<?> downloadDataAsExcel() {
        try {
            List<UserType> userTypeList = userTypeRepository.findAll();
            if (userTypeList.isEmpty()) {
                logger.info("No users available!");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users available!");
            }
            List<UserTypeResponseDto> userTypeResponseDtoList = userTypeList.stream().map(dtoUtilities::userTypeToUserTypeResponseDto)
                    .collect(Collectors.toList());
            String fileName = "usertype_excel_data.xlsx";
            String sheetName = fileName.substring(0,fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(userTypeResponseDtoList, sheetName);
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
    public ResponseEntity<?> downloadExcelSample() {
        try {
            String fileName = "usertype_excel_sample.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadExcelSample(UserTypeRequestDto.class, sheetName);
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

    @Override
    public ResponseEntity<?> uploadExcelFile(MultipartFile file, Class<?> currentClass) {
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

    @Override
    public ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse) {
        List<?> validList = validateDataResponse.getValidDataList();
        List<ValidatedData> invalidList = validateDataResponse.getInvalidDataList();
        List<UserTypeRequestDto> userTypeRequestDtoList = new ArrayList<>();
        for(int i=0;i<validList.size();)
        {
            ValidatedData validatedData = (ValidatedData) validList.get(i);
            UserTypeRequestDto userTypeRequestDto = (UserTypeRequestDto) validatedData.getData();
            boolean flag = isExistInDB(userTypeRequestDto);
            if(flag){
                ValidatedData invalidData = new ValidatedData();
                invalidData.setData(userTypeRequestDto);
                invalidData.setMessage("usertype already exist!");
                invalidList.add(invalidData);
                validList.remove(validatedData);
                continue;
            }
            userTypeRequestDtoList.add(userTypeRequestDto);
            i++;
        }
        validateDataResponse.setValidDataList(userTypeRequestDtoList);
        return validateDataResponse;
    }

    @Override
    public boolean isExistInDB(Object keyword) {
        UserTypeRequestDto userTypeRequestDto = (UserTypeRequestDto)keyword;
        Optional<UserType> userTypeOptional = userTypeRepository.findByUserTypeNameOrUserTypeCode(userTypeRequestDto.getUserTypeName(),userTypeRequestDto.getUserTypeCode());
        return userTypeOptional.isPresent();
    }

    @Override
    public ResponseEntity<?> saveBulkData(List<UserTypeRequestDto> list) {
        try{
            String username = jwtUtil.getCurrentLoginUser();
            if(username == null) {
                logger.info("Unauthenticated user!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated user!");
            }
            List<UserType> userList = list.stream()
                    .map((userTypeRequestDto)->{
                        UserType userType = dtoUtilities.userTypeRequestDtoToUserType(userTypeRequestDto);
                        userType.setCreatedBy(username);
                        userType.setUpdatedBy(username);
                        return userType;
                    })
                    .collect(Collectors.toList());
            userTypeRepository.saveAll(userList);
            logger.info("All data saved successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("All data saved successfully");
        }catch (Exception e){
            logger.error("Error occurred while saving bulk data, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving bulk data: "+e.getMessage());
        }
    }

    // usertype own methods
    public Optional<UserType> getUserType(String userTypeName){
        return userTypeRepository.findByUserTypeNameAndStatusTrue(userTypeName);
    }
}
