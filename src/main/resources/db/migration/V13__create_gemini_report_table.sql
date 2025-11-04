CREATE TABLE gemini_reports (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                admin_id UUID NOT NULL,
                                report_month DATE NOT NULL,
                                encrypted_content TEXT NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
