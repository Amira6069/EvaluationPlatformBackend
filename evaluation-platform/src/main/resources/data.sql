-- ===================================================================
-- GOVERNANCE EVALUATION PLATFORM - DATABASE INITIALIZATION
-- Inserts all 12 Governance Principles with Good Practices and Criteria
-- Based on the French governance framework from constants.js
-- ===================================================================

-- Clear existing data (optional - use with caution)
-- TRUNCATE TABLE criteria, good_practices, principles RESTART IDENTITY CASCADE;

-- ===================================================================
-- PRINCIPLE 1: Finalité (Purpose)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(1, 'Mission, vision et objectifs de l''organisme');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Définition de la mission', 'P1-GP1', (SELECT principle_id FROM principles WHERE number = 1)),
('Vision stratégique', 'P1-GP2', (SELECT principle_id FROM principles WHERE number = 1));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Une mission claire soit définie et documentée', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P1-GP1')),
('La mission soit communiquée à toutes les parties prenantes', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P1-GP1')),
('Une vision à long terme soit établie', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P1-GP2'));

-- ===================================================================
-- PRINCIPLE 2: Création de valeur (Value Creation)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(2, 'Création et distribution de valeur');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Mesure de la valeur créée', 'P2-GP1', (SELECT principle_id FROM principles WHERE number = 2));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Des indicateurs de création de valeur soient définis', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P2-GP1')),
('La valeur créée soit mesurée périodiquement', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P2-GP1'));

-- ===================================================================
-- PRINCIPLE 3: Stratégie (Strategy)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(3, 'Planification stratégique');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Plan stratégique', 'P3-GP1', (SELECT principle_id FROM principles WHERE number = 3));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Un plan stratégique pluriannuel soit élaboré', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P3-GP1')),
('Le plan stratégique soit révisé régulièrement', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P3-GP1'));

-- ===================================================================
-- PRINCIPLE 4: Surveillance (Monitoring)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(4, 'Systèmes de contrôle et surveillance');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Contrôle interne', 'P4-GP1', (SELECT principle_id FROM principles WHERE number = 4));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Un système de contrôle interne soit mis en place', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P4-GP1')),
('Des audits internes soient réalisés périodiquement', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P4-GP1'));

-- ===================================================================
-- PRINCIPLE 5: Redevabilité (Accountability)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(5, 'Responsabilité et transparence');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Information publique', 'P5-GP1', (SELECT principle_id FROM principles WHERE number = 5)),
('Reporting', 'P5-GP2', (SELECT principle_id FROM principles WHERE number = 5));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Un site web officiel existe', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P5-GP1')),
('Les informations financières sont publiées', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P5-GP1')),
('Des rapports annuels sont publiés', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P5-GP2'));

-- ===================================================================
-- PRINCIPLE 6: Dialogue avec les parties prenantes (Stakeholder Dialogue)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(6, 'Communication avec les parties prenantes');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Engagement des parties prenantes', 'P6-GP1', (SELECT principle_id FROM principles WHERE number = 6));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Les parties prenantes soient identifiées', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P6-GP1')),
('Des mécanismes de consultation soient établis', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P6-GP1'));

-- ===================================================================
-- PRINCIPLE 7: Leadership
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(7, 'Leadership et culture organisationnelle');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Gouvernance du conseil', 'P7-GP1', (SELECT principle_id FROM principles WHERE number = 7));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Un conseil d''administration/conseil soit constitué', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P7-GP1')),
('Le conseil se réunit régulièrement', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P7-GP1'));

-- ===================================================================
-- PRINCIPLE 8: Données et décisions (Data & Decisions)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(8, 'Gestion des données et prise de décision');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Gestion des données', 'P8-GP1', (SELECT principle_id FROM principles WHERE number = 8));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Un système de gestion des données soit mis en place', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P8-GP1')),
('Les décisions soient basées sur des données', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P8-GP1'));

-- ===================================================================
-- PRINCIPLE 9: Gouvernance de risque (Risk Governance)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(9, 'Gestion des risques');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Identification des risques', 'P9-GP1', (SELECT principle_id FROM principles WHERE number = 9));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Une cartographie des risques soit élaborée', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P9-GP1')),
('Des plans de mitigation soient définis', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P9-GP1'));

-- ===================================================================
-- PRINCIPLE 10: Responsabilité sociétale (Social Responsibility)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(10, 'Impact social et environnemental');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Impact social', 'P10-GP1', (SELECT principle_id FROM principles WHERE number = 10));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('L''impact social soit mesuré', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P10-GP1')),
('Des actions RSE soient mises en œuvre', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P10-GP1'));

-- ===================================================================
-- PRINCIPLE 11: Viabilité et pérennité (Sustainability)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(11, 'Durabilité de l''organisation');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Planification financière', 'P11-GP1', (SELECT principle_id FROM principles WHERE number = 11));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Un budget prévisionnel soit élaboré', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P11-GP1')),
('La viabilité financière soit assurée', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P11-GP1'));

-- ===================================================================
-- PRINCIPLE 12: Maîtrise de la corruption (Corruption Control)
-- ===================================================================
INSERT INTO principles (number, description) VALUES
(12, 'Prévention et lutte contre la corruption');

INSERT INTO good_practices (description, reference, principle_id) VALUES
('Code d''éthique', 'P12-GP1', (SELECT principle_id FROM principles WHERE number = 12)),
('Gestion des conflits d''intérêts', 'P12-GP2', (SELECT principle_id FROM principles WHERE number = 12));

INSERT INTO criteria (description, maturity_level, practice_id) VALUES
('Un code d''éthique soit élaboré', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P12-GP1')),
('Le personnel soit sensibilisé au code d''éthique', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P12-GP1')),
('Le code d''éthique soit signé par tous les membres', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P12-GP1')),
('Un processus de déclaration des intérêts soit défini', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P12-GP2')),
('Une politique des cadeaux soit mise en place', 0, (SELECT practice_id FROM good_practices WHERE reference = 'P12-GP2'));

-- ===================================================================
-- VERIFICATION QUERIES (Optional - for testing)
-- ===================================================================
-- SELECT COUNT(*) as total_principles FROM principles;
-- SELECT COUNT(*) as total_practices FROM good_practices;
-- SELECT COUNT(*) as total_criteria FROM criteria;

-- Expected results:
-- 12 principles
-- 17 good practices
-- 29 criteria