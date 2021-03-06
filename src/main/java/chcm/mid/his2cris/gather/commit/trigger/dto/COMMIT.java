package chcm.mid.his2cris.gather.commit.trigger.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class COMMIT {    
    private String id;//commit編號
    private long timestamp;
    private String commitStatus;//0:AUTO COMMIT=0 紅燈 收集之後的資料, 1:COMMIT 	綠燈 將這段資料開始做
    private List<TESTCLASS> testClass;
}
