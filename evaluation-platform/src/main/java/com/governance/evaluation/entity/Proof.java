package com.governance.evaluation.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "proofs")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Proof {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "proof_id")
	    private Long proofId;
	    
	    @Column(name = "file_name", nullable = false, length = 255)
	    private String fileName;
	    
	    @Column(name = "file_type", nullable = false, length = 50)
	    private String fileType;
	    
	    @Column(name = "file_path", nullable = false, length = 500)
	    private String filePath;
	    
	    @CreationTimestamp
	    @Column(name = "upload_date", updatable = false)
	    private LocalDateTime uploadDate;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "criteria_id", nullable = false)
	    private Criteria criteria;

}
