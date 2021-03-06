package chcm.mid.his2cris.gather.commit.trigger.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CHCM {    
    private String orgNo;//中心編號
    private String clientId;
    private String Uno;//檢查室代碼
    private String UID;//檢查人員員工編號
    private String IP;
    private String caseNo;//健檢號
    private String chartNo;//健檢號(不同table)
    private String examItem;//檢查項目編號
    private String status;//檢查狀態
    private String table;//table名稱
    private String action;//狀態(C:新增,U:更新,D:刪除)
    private String queryID;//AWS log message的ID
    private String cancelVal;//RPTTREPORTTBL cancelFlag=Y，刪除
    private long timestamp;
    private List<COMMIT> commit;
}
