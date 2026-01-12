-- Datu-basea sortu eta aukeratu
DROP DATABASE IF EXISTS birtek_db;
CREATE DATABASE IF NOT EXISTS birtek_db;

USE birtek_db;

-- ========================================================
-- 1. GEOGRAFIA 
-- ========================================================

-- Nazioa eta Lurraldea orain atributuak dira, ez taulak.
CREATE TABLE IF NOT EXISTS herriak (
    id_herria INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena VARCHAR(100) NOT NULL,
    lurraldea VARCHAR(100) NOT NULL, 
    nazioa VARCHAR(100) NOT NULL
);


-- ========================================================
-- 2. ENPRESA LANGILE eta Biltegi EGITURA (Sailak, Kontratu motak, eta biltegiak)
-- ========================================================

CREATE TABLE IF NOT EXISTS langile_sailak (
    id_saila INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena VARCHAR(100) NOT NULL,        
    kokapena VARCHAR(100) NOT NULL,     
    deskribapena TEXT                    
);

CREATE TABLE IF NOT EXISTS kontratu_motak (
    id_kontratua INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena VARCHAR(50) NOT NULL, 
    deskribapena TEXT,      
    soldata DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

-- ========================================================
-- 3. ERABILTZAILEAK (Langileak, Bezeroak, Hornitzaileak)
-- ========================================================

CREATE TABLE IF NOT EXISTS langileak (
    id_langilea INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena VARCHAR(50) NOT NULL,
    abizena VARCHAR(100) NOT NULL,
    nan VARCHAR(9) UNIQUE NOT NULL,
    jaiotza_data DATE NOT NULL, 
    
    -- Kokapena
    herria_id INT UNSIGNED NOT NULL,
    helbidea VARCHAR(150) NOT NULL,
    posta_kodea VARCHAR(5) NOT NULL,
    telefonoa VARCHAR(20) NOT NULL,
    
    -- Login datuak eta Hizkuntza
    emaila VARCHAR(100) UNIQUE NOT NULL,
    hizkuntza ENUM('Euskara', 'Gaztelania', 'Frantsesa', 'Ingelesa') NOT NULL DEFAULT 'Euskara', 
    pasahitza VARCHAR(255) NOT NULL, 
    salto_txartela_uid VARCHAR(50) UNIQUE, -- Langilea identifikatzeko Salto txartelaren kodea
    
    -- Lan datuak
    alta_data TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    aktibo BOOLEAN NOT NULL DEFAULT 0, -- BERRIA: Defektuz ez dago aktibo (0), onartu behar da
    
    saila_id INT UNSIGNED NOT NULL,        
    kontratua_id INT UNSIGNED NOT NULL,    
    iban VARCHAR(34) NOT NULL UNIQUE,
    
    CONSTRAINT fk_langilea_saila FOREIGN KEY (saila_id) REFERENCES langile_sailak(id_saila),
    CONSTRAINT fk_langilea_herria FOREIGN KEY (herria_id) REFERENCES herriak(id_herria),
    CONSTRAINT fk_langilea_kontratua FOREIGN KEY (kontratua_id) REFERENCES kontratu_motak(id_kontratua)
);

-- Fitxaketak (Langileen sarrera/irteera erregistroa)
CREATE TABLE IF NOT EXISTS fitxaketak (
    id_fitxaketa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    langilea_id INT UNSIGNED NOT NULL,
    data DATE NOT NULL DEFAULT (CURRENT_DATE),
    ordua TIME NOT NULL DEFAULT (CURRENT_TIME),
    mota ENUM('Sarrera', 'Irteera') NOT NULL DEFAULT 'Sarrera',
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_fitxaketa_langilea FOREIGN KEY (langilea_id) REFERENCES langileak(id_langilea)
);

CREATE TABLE IF NOT EXISTS bezeroak (
    id_bezeroa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena_edo_soziala VARCHAR(100) NOT NULL,
    abizena VARCHAR(100), 
    ifz_nan VARCHAR(9) UNIQUE NOT NULL, 
    jaiotza_data DATE,
    sexua ENUM ("gizona", "emakumea", "ez-binarioa"),
    
    -- Ordaintzeko beharrezkoa:
    bezero_ordainketa_txartela VARCHAR(255),  --  VARCHAR ordainketa pasarela egiteko
    
    -- Bidalketarako beharrezkoa
    helbidea VARCHAR(150) NOT NULL,
    herria_id INT UNSIGNED NOT NULL,
    posta_kodea VARCHAR(5) NOT NULL,
    telefonoa VARCHAR(15) NOT NULL, 
    
    -- Login egiteko beharrezkoa eta Hizkuntza
    emaila NVARCHAR(255) UNIQUE NOT NULL, 
    hizkuntza ENUM('Euskara', 'Gaztelania', 'Frantsesa', 'Ingelesa') NOT NULL DEFAULT 'Euskara',
    pasahitza VARCHAR(255) NOT NULL,      
    
    alta_data DATETIME DEFAULT CURRENT_TIMESTAMP,
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    aktibo BOOLEAN NOT NULL DEFAULT 1,
    
    CONSTRAINT fk_bezeroa_herria FOREIGN KEY (herria_id) REFERENCES herriak(id_herria)
);

CREATE TABLE IF NOT EXISTS hornitzaileak (
    id_hornitzailea INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena_soziala VARCHAR(100) NOT NULL,
    ifz_nan VARCHAR(9) UNIQUE NOT NULL, 
    
    -- Kontaktu datuak
    kontaktu_pertsona VARCHAR(100),
    helbidea VARCHAR(150) NOT NULL,
    herria_id INT UNSIGNED NOT NULL, 
    posta_kodea VARCHAR(5) NOT NULL,
    telefonoa VARCHAR (15) NOT NULL,
    
    -- Login datuak eta Hizkuntza
    emaila NVARCHAR(255) UNIQUE NOT NULL,   -- NVARCHAR kanpoko email helbideak ere ondo hartzeko
    hizkuntza ENUM('Euskara', 'Gaztelania', 'Frantsesa', 'Ingelesa') NOT NULL DEFAULT 'Gaztelania', 
    pasahitza VARCHAR(255) NOT NULL,
    aktibo BOOLEAN NOT NULL DEFAULT 1, -- BERRIA: Defektuz aktibo (1)
    
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_hornitzailea_herria FOREIGN KEY (herria_id) REFERENCES herriak(id_herria)
);

-- ========================================================
-- 4. PRODUKTU KATALOGOA,  ETA BILTEGIA
-- ========================================================

CREATE TABLE IF NOT EXISTS produktu_kategoriak (
    id_kategoria INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
    izena VARCHAR(50) UNIQUE NOT NULL 
);  

CREATE TABLE IF NOT EXISTS biltegiak (
    id_biltegia INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena VARCHAR(50) NOT NULL,        
    biltegi_sku VARCHAR(50) UNIQUE NOT NULL 
);

CREATE TABLE IF NOT EXISTS produktuak (
    id_produktua INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    hornitzaile_id INT UNSIGNED NOT NULL,
    kategoria_id INT UNSIGNED NOT NULL,
    izena VARCHAR(255) NOT NULL,
    marka VARCHAR(50) NOT NULL,  -- produktuaren marka-etxea
    mota ENUM('Generikoa', 'Eramangarria', 'Mahai-gainekoa', 'Mugikorra', 'Tableta', 'Zerbitzaria', 'Pantaila', 'Softwarea', 'Periferikoak', 'Kableak') NOT NULL DEFAULT 'Generikoa',
    
    deskribapena TEXT,  -- Produktuari buruz hornitzaileak zerbait jarri nahi badu
    irudia_url VARCHAR(255), -- Hornitzaileak igo dezake edota teknikariek igo dezakete
    
    -- Egoera  / Biltegiko langileak biltegia eta TEKNIKARIAK produktuari egoera ezarriko dio
    biltegi_id INT UNSIGNED,
    produktu_egoera ENUM('Berria', 'Berritua A', 'Berritua B','Hondatua','Zehazteko') NOT NULL DEFAULT 'Zehazteko',  -- teknikarien konponketetan aldatzeko
    produktu_egoera_oharra TEXT,   -- konponketa bakoitzeko teknikariek aldatu dezaketeena
    salgai BOOLEAN DEFAULT FALSE,  -- teknikariek salgai dagoela jartzeko
    
    -- Datu Ekonomikoak
    salmenta_prezioa DECIMAL(10, 2) NOT NULL DEFAULT 0.00, 
    stock INT UNSIGNED DEFAULT 0,
    eskaintza DECIMAL(5, 2) DEFAULT NULL, -- sekaintza produktuen atrubutua izango da / Adibidez: 20.00 (%20ko deskontua). NULL bada, ez dago eskaintzarik.

    
    
    sortze_data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_produktua_kategoria FOREIGN KEY (kategoria_id) REFERENCES produktu_kategoriak(id_kategoria),
    CONSTRAINT fk_produktua_hornitzailea FOREIGN KEY (hornitzaile_id) REFERENCES hornitzaileak(id_hornitzailea),
    CONSTRAINT fk_produktua_biltegia FOREIGN KEY (biltegi_id) REFERENCES biltegiak(id_biltegia)
);

    -- ========================================================
    -- SUBKLASEAK - PRODUKTUAK (HERENTZIA)
            -- Filtroak aplikatzeko produktuen kategoria ezberdinei. Mo
    -- ========================================================

    -- 1. Eramangarriak (Portátiles)
    CREATE TABLE IF NOT EXISTS eramangarriak (
        id_produktua INT UNSIGNED PRIMARY KEY,  -- PK eta FK batera
        prozesadorea VARCHAR(100), -- CPU
        ram_gb INT,
        diskoa_gb INT,
        pantaila_tamaina DECIMAL(4,1), -- Pulgadas
        bateria_wh INT,
        sistema_eragilea VARCHAR(50),
        pisua_kg DECIMAL(4,2),
        CONSTRAINT fk_eramangarria_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 2. Mahai-gainekoak (PC Sobremesa)
    CREATE TABLE IF NOT EXISTS mahai_gainekoak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        prozesadorea VARCHAR(100),
        plaka_basea VARCHAR(100),
        ram_gb INT,
        diskoa_gb INT,
        txartel_grafikoa VARCHAR(100),
        elikatze_iturria_w INT, -- Watts
        kaxa_formatua ENUM('ATX', 'Micro-ATX', 'Mini-ITX', 'E-ATX'),
        CONSTRAINT fk_mahaigainekoa_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 3. Mugikorrak (Smartphones)
    CREATE TABLE IF NOT EXISTS mugikorrak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        pantaila_teknologia VARCHAR(50), -- OLED, IPS...
        pantaila_hazbeteak DECIMAL(3,1),
        biltegiratzea_gb INT,
        ram_gb INT,
        kamera_nagusa_mp INT,
        bateria_mah INT,
        sistema_eragilea VARCHAR(50), -- Android, iOS
        sareak ENUM('4G', '5G'),
        CONSTRAINT fk_mugikorra_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 4. Tabletak (Tablets)
    CREATE TABLE IF NOT EXISTS tabletak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        pantaila_hazbeteak DECIMAL(4,1),
        biltegiratzea_gb INT,
        konektibitatea ENUM('WiFi', 'WiFi + Cellular'),
        sistema_eragilea VARCHAR(50), -- iPadOS, Android
        bateria_mah INT,
        arkatzarekin_bateragarria BOOLEAN DEFAULT FALSE,
        CONSTRAINT fk_tableta_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 5. Zerbitzariak (Servidores)
    CREATE TABLE IF NOT EXISTS zerbitzariak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        prozesadore_nukleoak INT,
        ram_mota ENUM('DDR4', 'DDR5', 'ECC'),
        disko_badiak INT, -- Número de bahías para discos
        rack_unitateak INT, -- 1U, 2U, 4U...
        elikatze_iturri_erredundantea BOOLEAN DEFAULT TRUE,
        raid_kontroladora VARCHAR(50),
        CONSTRAINT fk_zerbitzaria_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 6. Pantailak (Monitoreak)
    CREATE TABLE IF NOT EXISTS pantailak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        hazbeteak DECIMAL(4,1),
        bereizmena VARCHAR(20), -- 1920x1080, 4K...
        panel_mota ENUM('IPS', 'VA', 'TN', 'OLED'),
        freskatze_tasa_hz INT, -- 60Hz, 144Hz...
        konexioak VARCHAR(150), -- HDMI, DisplayPort...
        kurbatura VARCHAR(10), -- 1500R, Flat
        CONSTRAINT fk_pantaila_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 7. Softwareak
    CREATE TABLE IF NOT EXISTS softwareak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        software_mota ENUM('Sistema Eragilea', 'Ofimatika', 'Antibirusa', 'Bestelakoak') NOT NULL,
        lizentzia_mota ENUM('OEM', 'Retail', 'Harpidetza', 'OpenSource') DEFAULT 'Retail',
        bertsioa VARCHAR(50), -- Adib: "Windows 11 Pro", "Office 2021"
        garatzailea VARCHAR(100), -- Microsoft, Adobe, etab.
        
        CONSTRAINT fk_softwarea_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 8. Periferikoak 
    CREATE TABLE IF NOT EXISTS periferikoak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        periferiko_mota ENUM('Teklatua', 'Sagua', 'Aurikularrak', 'Bozgorailuak', 'Webkamera', 'Inprimagailua', 'Eskanerra', 'Bestelakoak') NOT NULL,
        konexioa VARCHAR(50), -- USB-C, Bluetooth, Haririk gabea...
        ezaugarriak TEXT, -- "Mekanikoa, RGB...", "16000 DPI..."
        argiztapena BOOLEAN DEFAULT FALSE, -- RGB edo argiztapena duen
        CONSTRAINT fk_periferikoak_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

    -- 9. Kableak 
    CREATE TABLE IF NOT EXISTS kableak (
        id_produktua INT UNSIGNED PRIMARY KEY,
        kable_mota ENUM('Bideoa', 'Datuak', 'Sarea', 'Audioa', 'Korrontea', 'Egokitzailea', 'Barnekoak', 'Bestelakoak') NOT NULL,
        luzera_m DECIMAL(4,2), -- 1.50, 3.00
        konektore_a VARCHAR(50), -- Adib: HDMI Arra
        konektore_b VARCHAR(50), -- Adib: HDMI Arra
        bertsioa VARCHAR(50), -- HDMI 2.1, CAT6, USB 3.0
        CONSTRAINT fk_kableak_produktua FOREIGN KEY (id_produktua) REFERENCES produktuak(id_produktua) ON DELETE CASCADE
    );

-- ========================================================
-- 4.1. TAILERRA ETA KONPONKETAK
-- ========================================================

-- Akatsen katalogoa (Zerrenda orokorra)
CREATE TABLE IF NOT EXISTS akatsak (
    id_akatsa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    izena VARCHAR(100) NOT NULL, -- Adib: "Pantaila hautsita", "Disko gogorraren errorea"
    deskribapena TEXT
);

-- Konponketa fitxa nagusia
CREATE TABLE IF NOT EXISTS konponketak (
    id_konponketa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    produktua_id INT UNSIGNED NOT NULL,
    langilea_id INT UNSIGNED NOT NULL, -- Teknikaria
    hasiera_data DATETIME DEFAULT CURRENT_TIMESTAMP,
    amaiera_data DATETIME, -- NULL da amaitu arte
    konponketa_egoera ENUM('Prozesuan', 'Konponduta', 'Konponezina') NOT NULL DEFAULT 'Prozesuan',
	akatsa_id INT UNSIGNED NOT NULL, -- Katalogoarekin lotura
    oharrak TEXT, -- Diagnostiko orokorra
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_konponketa_produktua FOREIGN KEY (produktua_id) REFERENCES produktuak(id_produktua),
    CONSTRAINT fk_konponketa_langilea FOREIGN KEY (langilea_id) REFERENCES langileak(id_langilea)
);

/*
-- Konponketa baten xehetasunak (Pausoz pauso edo akats zehatzak)
CREATE TABLE IF NOT EXISTS konponketa_lerroak (
    id_konponketa_lerroa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    konponketa_id INT UNSIGNED NOT NULL,
    akatsa_id INT UNSIGNED NOT NULL, -- Katalogoarekin lotura
    konponbidea TEXT, -- Nola konpondu den edo zer egin den
    
    CONSTRAINT fk_kl_konponketa FOREIGN KEY (konponketa_id) REFERENCES konponketak(id_konponketa),
    CONSTRAINT fk_kl_akatsa FOREIGN KEY (akatsa_id) REFERENCES akatsak(id_akatsa)
);
*/            

-- ========================================================
-- 5. LOGISTIKA (Sarrerak)
-- ========================================================

CREATE TABLE IF NOT EXISTS sarrerak (
    id_sarrera INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    data DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    hornitzailea_id INT UNSIGNED NOT NULL, 
    langilea_id INT UNSIGNED NOT NULL,     
    sarrera_egoera ENUM('Bidean', 'Jasota', "Ezabatua") NOT NULL DEFAULT 'Bidean',
    
    CONSTRAINT fk_sarrera_hornitzailea FOREIGN KEY (hornitzailea_id) REFERENCES hornitzaileak(id_hornitzailea),
    CONSTRAINT fk_sarrera_langilea FOREIGN KEY (langilea_id) REFERENCES langileak(id_langilea)
);

CREATE TABLE IF NOT EXISTS sarrera_lerroak (
    id_sarrera_lerroa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sarrera_id INT UNSIGNED NOT NULL,
    produktua_id INT UNSIGNED NOT NULL,
    kantitatea  INT UNSIGNED NOT NULL, 
    sarrera_lerro_egoera ENUM('Bidean', 'Jasota', "Ezabatua") NOT NULL DEFAULT 'Bidean',
    CONSTRAINT fk_sl_sarrera FOREIGN KEY (sarrera_id) REFERENCES sarrerak(id_sarrera),
    CONSTRAINT fk_sl_produktua FOREIGN KEY (produktua_id) REFERENCES produktuak(id_produktua)
);

-- ========================================================
-- 6. SALMENTAK (Eskaerak)
-- ========================================================

CREATE TABLE IF NOT EXISTS eskaerak (
    id_eskaera INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bezeroa_id INT UNSIGNED NOT NULL, 
    langilea_id INT UNSIGNED, 
    data DATETIME DEFAULT CURRENT_TIMESTAMP,
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    guztira_prezioa DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    
    eskaera_egoera ENUM('Prestatzen', 'Osatua/Bidalita', "Ezabatua") NOT NULL DEFAULT 'Prestatzen',
    
    CONSTRAINT fk_eskaera_bezeroa FOREIGN KEY (bezeroa_id) REFERENCES bezeroak(id_bezeroa),
    CONSTRAINT fk_eskaera_langilea FOREIGN KEY (langilea_id) REFERENCES langileak(id_langilea)
);

CREATE TABLE IF NOT EXISTS eskaera_lerroak (
    id_eskaera_lerroa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eskaera_id INT UNSIGNED NOT NULL, 
    produktua_id INT UNSIGNED NOT NULL, 
    kantitatea INT UNSIGNED NOT NULL, 
    unitate_prezioa DECIMAL(10, 2) NOT NULL, 
    
    
    eskaera_lerro_egoera ENUM('Prestatzen', 'Osatua/Bidalita', "Ezabatua") NOT NULL DEFAULT 'Prestatzen',
    
    
    
    CONSTRAINT fk_el_eskaera FOREIGN KEY (eskaera_id) REFERENCES eskaerak(id_eskaera),
    CONSTRAINT fk_el_produktua FOREIGN KEY (produktua_id) REFERENCES produktuak(id_produktua)
    
);

-- ========================================================
-- 7. FAKTURAZIOA
-- ========================================================

CREATE TABLE IF NOT EXISTS bezero_fakturak (
    id_faktura INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    faktura_zenbakia VARCHAR(20) UNIQUE NOT NULL,     
    eskaera_id INT UNSIGNED UNIQUE NOT NULL,   -- bezeroaren datuak hemendik aterako dira
    data DATE DEFAULT (CURRENT_DATE) NOT NULL,
    
    zergak_ehunekoa DECIMAL(5, 2) DEFAULT 21.00 NOT NULL, 
    fitxategia_url VARCHAR(255), 
    --
    CONSTRAINT fk_faktura_eskaera FOREIGN KEY (eskaera_id) REFERENCES eskaerak(id_eskaera)
);


-- ========================================================================================================================
-- INSERT INTO  datuen txertaketa
-- ========================================================================================================================

USE birtek_db;		
-- ============================================================
-- 1. OINARRIZKO DATUAK (PRODUKTUAK ETA LOGISTIKA)
-- ============================================================

-- 1.1 Biltegiak
INSERT INTO biltegiak (id_biltegia, izena, biltegi_sku) VALUES 
(1, 'Harrera Biltegia', 'HAR_BIL'),
(2, 'Biltegi Nagusia', 'BIL_NAG'),
(3, 'Irteera Biltegia', 'IRT_BIL');

--
-- 1.3 Kategoriak
INSERT INTO produktu_kategoriak (id_kategoria, izena) VALUES 
(1, 'Ordenagailuak'),
(2, 'Telefonia'),
(3, 'Irudia'),
(4, 'Osagarriak'),
(5, 'Softwarea'),
(6, 'Sareak eta Zerbitzariak');

-- ============================================================
-- 2. LANGILEEN MENDEKOTASUNAK
-- ============================================================

-- 2.1 Herriak (Orain Nazioa eta Lurraldea hemen barruan doaz)
INSERT INTO herriak (id_herria, izena, lurraldea, nazioa) VALUES
(1, 'Donostia', 'Gipuzkoa', 'Euskal Herria'),
(2, 'Bilbo', 'Bizkaia', 'Euskal Herria'),
(3, 'Gasteiz', 'Araba', 'Euskal Herria'),
(4, 'Iruña', 'Nafarroa', 'Euskal Herria'),
(5, 'Eibar', 'Gipuzkoa', 'Euskal Herria'),
(6, 'Zarautz', 'Gipuzkoa', 'Euskal Herria');

-- 2.2 Langile Sailak (Departamentuak)
INSERT INTO langile_sailak (id_saila, izena, kokapena, deskribapena) VALUES
(1, 'Zuzendaritza','Goi bulegoa', 'DIR'),
(2, 'Administrazioa','Harrera bulegoa', 'ADMIN'),
(3, 'Salmentak','Harrera', 'COM'),
(4, 'Zerbitzu Teknikoa','Tailerra', 'SAT'),
(5, 'Logistika eta Biltegia','Biltegiak', 'LOG');

-- 2.3 Kontratu Motak
INSERT INTO kontratu_motak (id_kontratua, izena, deskribapena, Soldata) VALUES
(1, 'Mugagabea Osoa', '40 ordu astero, kontratu finkoa', 1800.00),
(2, 'Mugagabea Partziala', 'Jardunaldi erdia, epe mugagabea', 900.00),
(3, 'Aldi Baterakoa', 'Obra edo zerbitzu jakin baterako', 1200.00),
(4, 'Praktikak', 'Formakuntza kontratua', 600.00),
(5, 'Zuzendaritza', 'Erantzukizun handiko postuak, helburuen araberakoa', 3500.00);

-- ============================================================
-- 3. LANGILEAK (Pasahitza: "1234")
-- ============================================================

INSERT INTO langileak (id_langilea, izena, abizena, nan, jaiotza_data, herria_id, helbidea, posta_kodea, telefonoa, emaila, pasahitza, salto_txartela_uid, saila_id, kontratua_id, iban, alta_data, aktibo) VALUES
-- 3.1 ZUZENDARITZA (SysAdmin) - Hauek aktibo jarriko ditugu zuzenean administratzaileak direlako
(1, 'Ander', 'Urien', '72484472H', '1992-03-02', 1, 'Askatasun Hiribidea 5', '20004', '600111222', 'ander@birtek.eus', '1234', 'UID_ADMIN_01', 1, 5, 'ES1234567890123456789001', '2020-01-01', 1),
(2, 'Lander', 'Garmendia', '12345678Z', '2000-03-02', 1, 'Askatasun Hiribidea 5', '20004', '600111222', 'lander@birtek.eus', '1234', 'UID_ADMIN_02', 1, 5, 'ES1234567890123456789002', '2020-01-01', 1),

-- 3.2 ADMINISTRAZIOA
(3, 'Ane', 'Lasa', '22222222B', '1985-03-20', 1, 'Easo Kalea 12', '20006', '600333444', 'ane.lasa@birtek.eus', '1234', 'UID_ADMIN_03', 2, 1, 'ES1234567890123456789003', '2020-02-15', 1),
(4, 'Mikel', 'Otegi', '33333333C', '1990-11-10', 6, 'Nafarroa Kalea 3', '20800', '600555666', 'mikel.otegi@birtek.eus', '1234', 'UID_ADMIN_04', 2, 2, 'ES1234567890123456789004', '2021-05-20', 1),

-- 3.3 SALMENTAK (Komertzialak)
(5, 'Leire', 'Mendizabal', '44444444D', '1992-07-08', 2, 'Gran Via 25', '48001', '600777888', 'leire.mendi@birtek.eus', '1234', 'UID_COM_01', 3, 1, 'ES1234567890123456789005', '2021-06-01', 1),
(6, 'Iker', 'Iriondo', '55555555E', '1995-01-30', 2, 'Licenciado Poza 10', '48011', '600999000', 'iker.iriondo@birtek.eus', '1234', 'UID_COM_02', 3, 3, 'ES1234567890123456789006', '2022-03-10', 1),
(7, 'Amaia', 'Goikoetxea', '66666666F', '1988-09-12', 5, 'Isasi Kalea 4', '20600', '600123123', 'amaia.goiko@birtek.eus', '1234', 'UID_COM_03', 3, 1, 'ES1234567890123456789007', '2019-11-05', 1),

-- 3.4 ZERBITZU TEKNIKOA (SAT)
(8, 'Unai', 'Zabala', '77777777G', '1993-04-25', 1, 'Tolosa Hiribidea 45', '20018', '600456456', 'unai.zabala@birtek.eus', '1234', 'UID_SAT_01', 4, 1, 'ES1234567890123456789008', '2020-09-15', 1),
(9, 'Maite', 'Arregi', '88888888H', '1996-12-05', 3, 'Dato Kalea 15', '01005', '600789789', 'maite.arregi@birtek.eus', '1234', 'UID_SAT_02', 4, 4, 'ES1234567890123456789009', '2023-01-10', 1),
(10, 'Aitor', 'Bilbao', '99999999I', '1991-08-18', 2, 'Indautxu Plaza 2', '48010', '600321321', 'aitor.bilbao@birtek.eus', '1234', 'UID_SAT_03', 4, 1, 'ES1234567890123456789010', '2021-02-28', 1),
(11, 'Nerea', 'Etxaniz', '12345678J', '1994-06-14', 6, 'Malekoia 8', '20800', '600654654', 'nerea.etxaniz@birtek.eus', '1234', 'UID_SAT_04', 4, 3, 'ES1234567890123456789011', '2022-07-20', 1),

-- 3.5 LOGISTIKA ETA BILTEGIA
(12, 'Gorka', 'Ugarte', '87654321K', '1987-02-22', 3, 'Gamarra Atea 4', '01013', '600987987', 'gorka.ugarte@birtek.eus', '1234', 'UID_LOG_01', 5, 1, 'ES1234567890123456789012', '2018-05-12', 1),
(13, 'Oihane', 'Ibarra', '23456789L', '1998-10-30', 3, 'Frantzia Kalea 20', '01002', '600147258', 'oihane.ibarra@birtek.eus', '1234', 'UID_LOG_02', 5, 3, 'ES1234567890123456789013', '2023-06-15', 1),
(14, 'Xabier', 'Larrea', '34567890M', '1990-03-15', 1, 'Intxaurrondo 50', '20015', '600258369', 'xabier.larrea@birtek.eus', '1234', 'UID_LOG_03', 5, 1, 'ES1234567890123456789014', '2020-11-30', 1);



-- 1.2 -Hornitzaileak
INSERT INTO hornitzaileak (id_hornitzailea, izena_soziala, ifz_nan, kontaktu_pertsona, helbidea, herria_id, posta_kodea, telefonoa, emaila, pasahitza) VALUES 
(1, 'PC Componentes Pro', 'A88776655', 'Soporte B2B', 'Poligono Industrial Alhama', 6, '28001', '910000000', 'b2b@pccomponentes.com', 'hash_pcc'),
(2, 'Ingram Micro', 'A11223344', 'Carlos Distribución', 'Calle Tecnología 5', 6, '28002', '910111222', 'pedidos@ingram.com', 'hash_ingram'),
(3, 'Amazon Business', 'W8888888', 'Logistika Zentroa', 'Trapagaran Poligonoa', 1, '48510', '900800700', 'business@amazon.es', 'hash_amazon');

INSERT INTO bezeroak (
    izena_edo_soziala, 
    abizena, 
    ifz_nan, 
    jaiotza_data, 
    sexua, 
    bezero_ordainketa_txartela, 
    helbidea, 
    herria_id, 
    posta_kodea, 
    telefonoa, 
    emaila, 
    hizkuntza, 
    pasahitza, 
    aktibo		
) VALUES 
-- 1. Bezero estandarra (Emakumea, Euskara, Donostia)
(
    'Ane', 
    'Goikoetxea Lasa', 
    '12345678A', 
    '1990-05-15', 
    'emakumea', 
    'tok_visa_42424242', -- Pasarelak emandako token simulazioa
    'Askatasunaren Etorbidea 14, 2.B', 
    1, 
    '20004', 
    '600123456', 
    'ane.goiko@email.eus', 
    'Euskara', 
    'pasahitzaSegurua1', 
    1
),
-- 2. Bezeroa (Gizona, Gaztelania, Bilbo)
(
    'Jon', 
    'Perez Garcia', 
    '87654321B', 
    '1985-11-20', 
    'gizona', 
    'tok_mastercard_5555', 
    'Kale Nagusia 30', 
    2, 
    '48001', 
    '611222333', 
    'jon.perez@gmail.com', 
    'Gaztelania', 
    '123456Jon', 
    1
),
-- 3. Enpresa bat (Ez du generorik, ez abizenik, Gasteiz)
(
    'Teknologia Berriak SL', 
    NULL, -- Enpresek ez dute abizenik
    'B99887766', 
    NULL, -- Ez dute jaiotza datarik
    NULL, -- Ez dute generorik (ENUM-ak NULL onartzen du NOT NULL ez badago)
    'tok_amex_9090', 
    'Jundiz Industrialdea, Pab 5', 
    3, 
    '01015', 
    '945111222', 
    'info@teknologiaberriak.com', 
    'Euskara', 
    'admin2024', 
    1
),
-- 4. Bezero gaztea (Ez-binarioa, Ingelesa, Iruñea)
(
    'Alex', 
    'Etxebarria', 
    '44556677C', 
    '2002-02-10', 
    'ez-binarioa', 
    NULL, -- Oraindik ez du txartelik sartu
    'Estafeta Kalea 12', 
    4, 
    '31001', 
    '666777888', 
    'alex.etxe@protonmail.com', 
    'Ingelesa', 
    'alex_pass_secure', 
    1
),
-- 5. Atzerritarra (Emakumea, Frantsesa, Biarritz/Miarritze)
(
    'Sarah', 
    'Dubois', 
    'X1234567Z', -- NIE formatua
    '1995-07-30', 
    'emakumea', 
    'tok_cb_3333', 
    'Rue du Port 5', 
    5, 
    '64200', 
    '+33612345678', 
    'sarah.dubois@orange.fr', 
    'Frantsesa', 
    'monmotdepasse', 
    0 -- Oraindik ez du kontua aktibatu (emaila baieztatu gabe)
);	

-- ============================================================
-- 4. PRODUKTUAK TXERTATU
-- ============================================================

-- ------------------------------------------------------------
-- 1-10: ERAMANGARRIAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(1, 'MacBook Pro 14', 'M3 txiparekin, potentzia handia diseinu trinkoan.', 1, 1, 1, 'Apple', 'Eramangarria', 'Berria', 2199.00, 15, TRUE, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca4?w=800&q=80'),
(2, 'Dell XPS 13 Plus', 'Pantaila ia ertz gabea, Windows 11rako optimizatua.', 1, 1, 1, 'Dell', 'Eramangarria', 'Berria', 1499.00, 10, TRUE, 'https://images.unsplash.com/photo-1593642632823-8f78536788c6?w=800&q=80'),
(3, 'Lenovo ThinkPad X1', 'Enpresentzako estandarra, karbono zuntzezko akaberarekin.', 2, 2, 1, 'Lenovo', 'Eramangarria', 'Berria', 1850.00, 20, TRUE, 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&q=80'),
(4, 'HP Spectre x360', 'Bihurgarria, tablet moduan erabil daiteke.', 1, 1, 1, 'HP', 'Eramangarria', 'Berritua A', 950.00, 5, TRUE, 'https://images.unsplash.com/photo-1544731612-de7f96afe55f?w=800&q=80'),
(5, 'Asus ROG Zephyrus', 'Gaming eramangarria, RTX 4070 grafikoarekin.', 1, 2, 1, 'Asus', 'Eramangarria', 'Berria', 2300.00, 8, TRUE, 'https://images.unsplash.com/photo-1626218174358-77b797576569?w=800&q=80'),
(6, 'Acer Swift 5', 'Oso arina, bidaiatzeko aproposa.', 2, 1, 1, 'Acer', 'Eramangarria', 'Berria', 899.00, 12, TRUE, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&q=80'),
(7, 'Microsoft Surface Laptop 5', 'Ukimen-pantaila eta akabera metalikoa.', 1, 1, 1, 'Microsoft', 'Eramangarria', 'Berritua B', 750.00, 3, TRUE, 'https://images.unsplash.com/photo-1531297461136-82lw8279148?w=800&q=80'),
(8, 'Razer Blade 15', 'Diseinu beltza, RGB teklatua eta potentzia gorena.', 1, 2, 1, 'Razer', 'Eramangarria', 'Berria', 2800.00, 4, TRUE, 'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=800&q=80'),
(9, 'LG Gram 17', '17 hazbeteko pantaila baina pisu oso txikia.', 2, 1, 1, 'LG', 'Eramangarria', 'Berria', 1350.00, 7, TRUE, 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&q=80'),
(10, 'MacBook Air M2', 'Isila, haizagailurik gabea eta bateria luzea.', 1, 1, 1, 'Apple', 'Eramangarria', 'Berria', 1299.00, 25, TRUE, 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=800&q=80');

INSERT INTO eramangarriak (id_produktua, prozesadorea, ram_gb, diskoa_gb, pantaila_tamaina, bateria_wh, sistema_eragilea, pisua_kg) VALUES
(1, 'Apple M3 Pro', 18, 512, 14.2, 70, 'macOS', 1.6),
(2, 'Intel Core i7-1360P', 16, 1024, 13.4, 55, 'Windows 11', 1.23),
(3, 'Intel Core i7-1260P', 32, 1024, 14.0, 57, 'Windows 11 Pro', 1.12),
(4, 'Intel Core i5-1135G7', 8, 256, 13.5, 60, 'Windows 10', 1.3),
(5, 'AMD Ryzen 9 7940HS', 32, 2048, 14.0, 76, 'Windows 11', 1.7),
(6, 'Intel Core i5-1240P', 16, 512, 14.0, 56, 'Windows 11', 1.2),
(7, 'Intel Core i5-1235U', 8, 256, 13.5, 47, 'Windows 11', 1.27),
(8, 'Intel Core i9-13800H', 32, 1024, 15.6, 80, 'Windows 11', 2.01),
(9, 'Intel Core i7-1360P', 16, 1024, 17.0, 80, 'Windows 11', 1.35),
(10, 'Apple M2', 8, 256, 13.6, 52, 'macOS', 1.24);

-- ------------------------------------------------------------
-- 11-20: MAHAI-GAINEKOAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(11, 'HP Omen 45L', 'Gaming dorre aurreratua hozte sistema bereziarekin.', 1, 2, 1, 'HP', 'Mahai-gainekoa', 'Berria', 2499.00, 5, TRUE, 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?w=800&q=80'),
(12, 'Dell OptiPlex 7000', 'Bulegorako ordenagailu trinkoa eta fidagarria.', 2, 1, 1, 'Dell', 'Mahai-gainekoa', 'Berria', 650.00, 30, TRUE, 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=800&q=80'),
(13, 'Apple Mac Mini M2', 'Txikia baina matoia, mahaigain garbietarako.', 1, 1, 1, 'Apple', 'Mahai-gainekoa', 'Berria', 699.00, 15, TRUE, 'https://images.unsplash.com/photo-1527443195645-1133f7f28990?w=800&q=80'),
(14, 'Lenovo Legion Tower', 'RGB argiak eta RTX grafikoa jokoetarako.', 1, 2, 1, 'Lenovo', 'Mahai-gainekoa', 'Berria', 1200.00, 8, TRUE, 'https://images.unsplash.com/photo-1605722243979-fe0be81929d9?w=800&q=80'),
(15, 'Custom PC Creator', 'Eduki sortzaileentzako muntatutako ordenagailua.', 2, 1, 1, 'Custom', 'Mahai-gainekoa', 'Berritua A', 1100.00, 2, TRUE, 'https://images.unsplash.com/photo-1555618568-9b1689958e7b?w=800&q=80'),
(16, 'Corsair One i300', 'Formatu oso txikia (Mini-ITX) baina oso potentea.', 1, 2, 1, 'Corsair', 'Mahai-gainekoa', 'Berria', 3500.00, 2, TRUE, 'https://images.unsplash.com/photo-1591488320449-011701bb6704?w=800&q=80'),
(17, 'Acer Predator Orion', 'Diseinu futurista eta aireztapen bikaina.', 1, 2, 1, 'Acer', 'Mahai-gainekoa', 'Berria', 1800.00, 4, TRUE, 'https://images.unsplash.com/photo-1580894732444-8ecded7900cd?w=800&q=80'),
(18, 'HP All-in-One 27', 'Ordenagailua eta pantaila dena batean.', 2, 1, 1, 'HP', 'Mahai-gainekoa', 'Berria', 950.00, 10, TRUE, 'https://images.unsplash.com/photo-1542393545-10f5cde2c81d?w=800&q=80'),
(19, 'MSI Trident 3', 'Kontsola itxurako PCa, egongelarako.', 1, 2, 1, 'MSI', 'Mahai-gainekoa', 'Berritua B', 800.00, 3, TRUE, 'https://images.unsplash.com/photo-1534030635237-77b5a8e0f5b9?w=800&q=80'),
(20, 'Apple Mac Studio', 'Profesionalentzako errendimendu maximoa.', 1, 1, 1, 'Apple', 'Mahai-gainekoa', 'Berria', 2399.00, 6, TRUE, 'https://images.unsplash.com/photo-1647427060118-4911c9821b82?w=800&q=80');

INSERT INTO mahai_gainekoak (id_produktua, prozesadorea, plaka_basea, ram_gb, diskoa_gb, txartel_grafikoa, elikatze_iturria_w, kaxa_formatua) VALUES
(11, 'Intel Core i9-13900K', 'Z790', 64, 2048, 'RTX 4090', 1200, 'ATX'),
(12, 'Intel Core i5-12500', 'B660', 16, 512, 'Intel UHD 770', 300, 'Micro-ATX'),
(13, 'Apple M2 Chip', 'Integrated', 8, 256, 'Apple GPU 10-core', 150, 'Mini-ITX'),
(14, 'AMD Ryzen 7 5800X', 'B550', 32, 1024, 'RTX 3070', 750, 'ATX'),
(15, 'AMD Ryzen 9 5900X', 'X570', 64, 2048, 'RX 6800 XT', 850, 'ATX'),
(16, 'Intel Core i9-12900K', 'Z690I', 32, 2048, 'RTX 3080 Ti', 750, 'Mini-ITX'),
(17, 'Intel Core i7-12700K', 'Z690', 32, 1024, 'RTX 3080', 800, 'ATX'),
(18, 'Intel Core i7-12700T', 'Custom HP', 16, 512, 'Intel Iris Xe', 180, 'Mini-ITX'),
(19, 'Intel Core i5-11400F', 'H510', 16, 512, 'RTX 3060 Aero', 450, 'Mini-ITX'),
(20, 'Apple M1 Max', 'Integrated', 32, 512, 'Apple GPU 24-core', 370, 'Mini-ITX');

-- ------------------------------------------------------------
-- 21-35: MUGIKORRAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(21, 'iPhone 15 Pro', 'Titaniozko gorputza eta A17 Pro txipa.', 1, 1, 2, 'Apple', 'Mugikorra', 'Berria', 1209.00, 50, TRUE, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&q=80'),
(22, 'Samsung Galaxy S24 Ultra', 'AI integratua eta S-Pen arkatza barne.', 2, 1, 2, 'Samsung', 'Mugikorra', 'Berria', 1459.00, 40, TRUE, 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=800&q=80'),
(23, 'Google Pixel 8 Pro', 'Kamera adimenduna eta Android garbia.', 1, 2, 2, 'Google', 'Mugikorra', 'Berria', 1099.00, 20, TRUE, 'https://images.unsplash.com/photo-1598327105666-5b89351aff23?w=800&q=80'),
(24, 'Xiaomi 13T Pro', 'Leica kamerak eta karga ultra-azkarra.', 2, 2, 2, 'Xiaomi', 'Mugikorra', 'Berria', 799.00, 25, TRUE, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80'),
(25, 'OnePlus 11', 'Errendimendu bikaina prezio lehiakorrean.', 2, 1, 2, 'OnePlus', 'Mugikorra', 'Berritua A', 550.00, 10, TRUE, 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=800&q=80'),
(26, 'Sony Xperia 1 V', 'Zinemako pantaila formatua (21:9).', 1, 2, 2, 'Sony', 'Mugikorra', 'Berria', 1100.00, 5, TRUE, 'https://images.unsplash.com/photo-1523206489230-c012c64b2b48?w=800&q=80'),
(27, 'Nothing Phone (2)', 'Atzealde gardena eta LED interfazea.', 2, 1, 2, 'Nothing', 'Mugikorra', 'Berria', 650.00, 15, TRUE, 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=800&q=80'),
(28, 'Samsung Galaxy Z Flip 5', 'Tolestagarria eta poltsikoan eramateko erosoa.', 2, 1, 2, 'Samsung', 'Mugikorra', 'Berria', 999.00, 12, TRUE, 'https://images.unsplash.com/photo-1603539286469-650630b42796?w=800&q=80'),
(29, 'iPhone 13 Mini', 'Tamaina txikia, esku bakarrarekin erabiltzeko.', 1, 1, 2, 'Apple', 'Mugikorra', 'Berritua B', 450.00, 8, TRUE, 'https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=800&q=80'),
(30, 'Motorola Edge 40', 'Diseinu mehea eta pantaila kurbatua.', 2, 2, 2, 'Motorola', 'Mugikorra', 'Berria', 499.00, 20, TRUE, 'https://images.unsplash.com/photo-1570891836654-d4961a7b6929?w=800&q=80'),
(31, 'Asus Zenfone 10', 'Trinkoa baina oso indartsua.', 1, 1, 2, 'Asus', 'Mugikorra', 'Berria', 750.00, 8, TRUE, 'https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=800&q=80'),
(32, 'Realme GT 3', 'Munduko kargatze azkarrena (240W).', 2, 2, 2, 'Realme', 'Mugikorra', 'Berria', 600.00, 10, TRUE, 'https://images.unsplash.com/photo-1605236453806-6ff36851218e?w=800&q=80'),
(33, 'iPhone SE (2022)', 'Klasikoa TouchID botoiarekin.', 1, 1, 2, 'Apple', 'Mugikorra', 'Berria', 429.00, 30, TRUE, 'https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=800&q=80'),
(34, 'Honor Magic 5 Pro', 'Kamera sistema oso aurreratua.', 2, 2, 2, 'Honor', 'Mugikorra', 'Berria', 900.00, 7, TRUE, 'https://images.unsplash.com/photo-1560529178-85566d4291f7?w=800&q=80'),
(35, 'Oppo Find X5', 'Diseinu futurista eta material premiumak.', 2, 1, 2, 'Oppo', 'Mugikorra', 'Berritua A', 400.00, 5, TRUE, 'https://images.unsplash.com/photo-1616348436168-de43ad0db179?w=800&q=80');

INSERT INTO mugikorrak (id_produktua, pantaila_teknologia, pantaila_hazbeteak, biltegiratzea_gb, ram_gb, kamera_nagusa_mp, bateria_mah, sistema_eragilea, sareak) VALUES
(21, 'OLED', 6.1, 128, 8, 48, 3274, 'iOS', '5G'),
(22, 'AMOLED', 6.8, 256, 12, 200, 5000, 'Android', '5G'),
(23, 'OLED', 6.7, 128, 12, 50, 5050, 'Android', '5G'),
(24, 'AMOLED', 6.6, 256, 12, 50, 5000, 'Android', '5G'),
(25, 'AMOLED', 6.7, 128, 8, 50, 5000, 'Android', '5G'),
(26, 'OLED', 6.5, 256, 12, 48, 5000, 'Android', '5G'),
(27, 'OLED', 6.7, 256, 12, 50, 4700, 'Android', '5G'),
(28, 'AMOLED', 6.7, 256, 8, 12, 3700, 'Android', '5G'),
(29, 'OLED', 5.4, 64, 4, 12, 2438, 'iOS', '5G'),
(30, 'OLED', 6.5, 256, 8, 50, 4400, 'Android', '5G'),
(31, 'AMOLED', 5.9, 128, 8, 50, 4300, 'Android', '5G'),
(32, 'AMOLED', 6.7, 256, 16, 50, 4600, 'Android', '5G'),
(33, 'LCD', 4.7, 64, 4, 12, 2018, 'iOS', '5G'),
(34, 'OLED', 6.8, 512, 12, 50, 5100, 'Android', '5G'),
(35, 'AMOLED', 6.5, 256, 8, 50, 4800, 'Android', '5G');

-- ------------------------------------------------------------
-- 36-45: TABLETAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(36, 'iPad Pro 12.9', 'M2 txipa eta Liquid Retina XDR pantaila.', 1, 1, 2, 'Apple', 'Tableta', 'Berria', 1449.00, 10, TRUE, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&q=80'),
(37, 'Samsung Galaxy Tab S9', 'Urarekiko erresistentzia eta pantaila bikaina.', 2, 1, 2, 'Samsung', 'Tableta', 'Berria', 899.00, 15, TRUE, 'https://images.unsplash.com/photo-1585790050230-5dd28404ccb9?w=800&q=80'),
(38, 'iPad Air 5', 'M1 txipa, oreka perfektua potentzia eta prezioan.', 1, 1, 2, 'Apple', 'Tableta', 'Berria', 769.00, 20, TRUE, 'https://images.unsplash.com/photo-1542751110-97427bbecf20?w=800&q=80'),
(39, 'Microsoft Surface Pro 9', 'Ordenagailua eta tableta gailu berean.', 1, 2, 2, 'Microsoft', 'Tableta', 'Berria', 1100.00, 8, TRUE, 'https://images.unsplash.com/photo-1542125565-d63654406393?w=800&q=80'),
(40, 'Lenovo Tab P11 Pro', 'Multimedia kontsumorako aproposa.', 2, 2, 2, 'Lenovo', 'Tableta', 'Berritua A', 350.00, 12, TRUE, 'https://images.unsplash.com/photo-1561154464-82e9adf32764?w=800&q=80'),
(41, 'Xiaomi Pad 6', 'Kalitate/prezio erlazio ezin hobea.', 2, 2, 2, 'Xiaomi', 'Tableta', 'Berria', 399.00, 25, TRUE, 'https://images.unsplash.com/photo-1589739900243-4b52cd9b104e?w=800&q=80'),
(42, 'iPad Mini 6', 'Oso txikia, oharrak hartzeko perfektua.', 1, 1, 2, 'Apple', 'Tableta', 'Berria', 649.00, 10, TRUE, 'https://images.unsplash.com/photo-1525909002-1b05e0c869d8?w=800&q=80'),
(43, 'Amazon Fire Max 11', 'Oinarrizko erabilerarako eta irakurketarako.', 2, 1, 2, 'Amazon', 'Tableta', 'Berria', 249.00, 30, TRUE, 'https://images.unsplash.com/photo-1594961556811-3e4b772913db?w=800&q=80'),
(44, 'Huawei MatePad Pro', 'HarmonyOS sistema eragilearekin.', 2, 2, 2, 'Huawei', 'Tableta', 'Zehazteko', 500.00, 5, FALSE, 'https://images.unsplash.com/photo-1558564028-2b4700d93133?w=800&q=80'),
(45, 'Google Pixel Tablet', 'Base bozgorailuarekin dator.', 1, 1, 2, 'Google', 'Tableta', 'Berria', 679.00, 7, TRUE, 'https://images.unsplash.com/photo-1629891404111-2b8109312948?w=800&q=80');

INSERT INTO tabletak (id_produktua, pantaila_hazbeteak, biltegiratzea_gb, konektibitatea, sistema_eragilea, bateria_mah, arkatzarekin_bateragarria) VALUES
(36, 12.9, 256, 'WiFi + Cellular', 'iPadOS', 10758, TRUE),
(37, 11.0, 128, 'WiFi', 'Android', 8400, TRUE),
(38, 10.9, 64, 'WiFi', 'iPadOS', 7600, TRUE),
(39, 13.0, 256, 'WiFi', 'Windows 11', 6500, TRUE),
(40, 11.2, 128, 'WiFi', 'Android', 8000, TRUE),
(41, 11.0, 128, 'WiFi', 'Android', 8840, TRUE),
(42, 8.3, 64, 'WiFi + Cellular', 'iPadOS', 5124, TRUE),
(43, 11.0, 64, 'WiFi', 'FireOS', 7500, FALSE),
(44, 12.6, 256, 'WiFi', 'HarmonyOS', 10050, TRUE),
(45, 10.9, 128, 'WiFi', 'Android', 7020, TRUE);

-- ------------------------------------------------------------
-- 46-50: ZERBITZARIAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(46, 'Dell PowerEdge R750', 'Rack zerbitzaria datu zentroetarako.', 1, 2, 6, 'Dell', 'Zerbitzaria', 'Berria', 4500.00, 2, TRUE, 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800&q=80'),
(47, 'HPE ProLiant DL380', 'Munduko zerbitzaririk salduena, fidagarria.', 1, 2, 6, 'HPE', 'Zerbitzaria', 'Berria', 3800.00, 3, TRUE, 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&q=80'),
(48, 'Lenovo ThinkSystem SR650', 'Eskalagarria eta errendimendu altukoa.', 2, 2, 6, 'Lenovo', 'Zerbitzaria', 'Berritua A', 2200.00, 1, TRUE, 'https://images.unsplash.com/photo-1597852074816-d933c7d2b988?w=800&q=80'),
(49, 'Synology RackStation', 'Biltegiratze masiborako NAS zerbitzaria.', 2, 1, 6, 'Synology', 'Zerbitzaria', 'Berria', 1500.00, 5, TRUE, 'https://images.unsplash.com/photo-1544197150-b99a580bbcbf?w=800&q=80'),
(50, 'Cisco UCS C220', 'Birtualizaziorako optimizatua.', 1, 2, 6, 'Cisco', 'Zerbitzaria', 'Berritua B', 1200.00, 2, TRUE, 'https://images.unsplash.com/photo-1551703599-6b3e8379aa8c?w=800&q=80');

INSERT INTO zerbitzariak (id_produktua, prozesadore_nukleoak, ram_mota, disko_badiak, rack_unitateak, elikatze_iturri_erredundantea, raid_kontroladora) VALUES
(46, 24, 'DDR5', 12, 2, TRUE, 'PERC H750'),
(47, 20, 'DDR4', 8, 2, TRUE, 'HPE Smart Array'),
(48, 16, 'DDR4', 16, 2, TRUE, 'ThinkSystem RAID'),
(49, 4, 'DDR4', 12, 2, TRUE, 'Software RAID'),
(50, 12, 'DDR4', 8, 1, TRUE, 'Cisco 12G SAS');

-- ------------------------------------------------------------
-- 51-65: PANTAILAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(51, 'LG UltraGear 27', 'Gaming pantaila azkarra 144Hz-ekin.', 2, 1, 3, 'LG', 'Pantaila', 'Berria', 299.00, 20, TRUE, 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&q=80'),
(52, 'Dell UltraSharp 32', '4K bereizmena diseinu grafikorako.', 1, 1, 3, 'Dell', 'Pantaila', 'Berria', 850.00, 5, TRUE, 'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&q=80'),
(53, 'Samsung Odyssey G9', 'Pantaila ultra-zovala eta kurbatua.', 2, 2, 3, 'Samsung', 'Pantaila', 'Berria', 1200.00, 3, TRUE, 'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=800&q=80'),
(54, 'BenQ PD2700U', 'Kolore zehatzak (sRGB 100%).', 2, 1, 3, 'BenQ', 'Pantaila', 'Berritua A', 350.00, 8, TRUE, 'https://images.unsplash.com/photo-1585792180666-f7347c490ee2?w=800&q=80'),
(55, 'Asus ProArt', 'Argazkilari eta bideo editoreentzako.', 1, 1, 3, 'Asus', 'Pantaila', 'Berria', 500.00, 6, TRUE, 'https://images.unsplash.com/photo-1586210579191-3dbb7975f782?w=800&q=80'),
(56, 'HP 24mh', 'Bulegorako oinarrizko pantaila ergonomikoa.', 2, 1, 3, 'HP', 'Pantaila', 'Berria', 149.00, 40, TRUE, 'https://images.unsplash.com/photo-1515630278258-407f66498911?w=800&q=80'),
(57, 'MSI Optix MAG', 'Pantaila kurbatua murgiltze esperientziarako.', 1, 2, 3, 'MSI', 'Pantaila', 'Berria', 220.00, 15, TRUE, 'https://images.unsplash.com/photo-1586952518485-11b180e92764?w=800&q=80'),
(58, 'Apple Studio Display', '5K bereizmena eta eraikuntza bikaina.', 1, 1, 3, 'Apple', 'Pantaila', 'Berria', 1779.00, 4, TRUE, 'https://images.unsplash.com/photo-1510511459019-5dda7724fd87?w=800&q=80'),
(59, 'Philips Brilliance', 'Ultrawide formatua produktibitaterako.', 2, 1, 3, 'Philips', 'Pantaila', 'Berritua B', 400.00, 5, TRUE, 'https://images.unsplash.com/photo-1461151304267-38535e780c79?w=800&q=80'),
(60, 'AOC 24G2', 'Prezio-kalitate erlazio onena jokoetarako.', 2, 2, 3, 'AOC', 'Pantaila', 'Berria', 179.00, 25, TRUE, 'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=800&q=80'),
(61, 'ViewSonic Elite', 'Kolore biziak eta erantzun azkarra.', 1, 2, 3, 'ViewSonic', 'Pantaila', 'Berria', 550.00, 7, TRUE, 'https://images.unsplash.com/photo-1542393545-10f5cde2c81d?w=800&q=80'),
(62, 'Lenovo ThinkVision', 'USB-C konexioa duen dock integratua.', 2, 1, 3, 'Lenovo', 'Pantaila', 'Berria', 320.00, 12, TRUE, 'https://images.unsplash.com/photo-1551636898-47668aa61de2?w=800&q=80'),
(63, 'Gigabyte M27Q', 'KVM switch integratua.', 1, 2, 3, 'Gigabyte', 'Pantaila', 'Berria', 340.00, 9, TRUE, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&q=80'),
(64, 'Eizo ColorEdge', 'Profesionalentzako kalibrazio zehatza.', 2, 1, 3, 'Eizo', 'Pantaila', 'Zehazteko', 1200.00, 2, FALSE, 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&q=80'),
(65, 'Huawei MateView', 'Formatu karratuagoa (3:2) kodetzeko.', 2, 2, 3, 'Huawei', 'Pantaila', 'Berria', 599.00, 6, TRUE, 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=800&q=80');

INSERT INTO pantailak (id_produktua, hazbeteak, bereizmena, panel_mota, freskatze_tasa_hz, konexioak, kurbatura) VALUES
(51, 27.0, '2560x1440', 'IPS', 144, 'HDMI, DP', 'Flat'),
(52, 32.0, '3840x2160', 'IPS', 60, 'HDMI, DP, USB-C', 'Flat'),
(53, 49.0, '5120x1440', 'VA', 240, 'HDMI, DP', '1000R'),
(54, 27.0, '3840x2160', 'IPS', 60, 'HDMI, DP', 'Flat'),
(55, 27.0, '2560x1440', 'IPS', 75, 'HDMI, DP, USB-C', 'Flat'),
(56, 23.8, '1920x1080', 'IPS', 75, 'HDMI, VGA', 'Flat'),
(57, 24.0, '1920x1080', 'VA', 165, 'HDMI, DP', '1500R'),
(58, 27.0, '5120x2880', 'IPS', 60, 'Thunderbolt 3', 'Flat'),
(59, 34.0, '3440x1440', 'VA', 100, 'HDMI, DP', '1500R'),
(60, 24.0, '1920x1080', 'IPS', 144, 'HDMI, DP', 'Flat'),
(61, 32.0, '2560x1440', 'IPS', 175, 'HDMI, DP', 'Flat'),
(62, 27.0, '2560x1440', 'IPS', 60, 'USB-C, HDMI', 'Flat'),
(63, 27.0, '2560x1440', 'IPS', 170, 'HDMI, DP, USB-C', 'Flat'),
(64, 27.0, '2560x1440', 'IPS', 60, 'HDMI, DP, DVI', 'Flat'),
(65, 28.2, '3840x2560', 'IPS', 60, 'MiniDP, HDMI', 'Flat');

-- ------------------------------------------------------------
-- 66-75: SOFTWAREA
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(66, 'Windows 11 Pro', 'Sistema eragile berriena profesionalentzat.', 3, 1, 5, 'Microsoft', 'Softwarea', 'Berria', 145.00, 100, TRUE, 'https://images.unsplash.com/photo-1662947036644-8898ae415b36?w=800&q=80'),
(67, 'Office 2021 Home', 'Word, Excel eta PowerPoint bizitza osorako.', 3, 1, 5, 'Microsoft', 'Softwarea', 'Berria', 149.00, 50, TRUE, 'https://images.unsplash.com/photo-1631553127888-29007f354f3b?w=800&q=80'),
(68, 'Adobe Photoshop CC', '1 urteko harpidetza diseinatzaileentzat.', 3, 2, 5, 'Adobe', 'Softwarea', 'Berria', 290.00, 100, TRUE, 'https://images.unsplash.com/photo-1626785774573-4b7993125486?w=800&q=80'),
(69, 'Kaspersky Total Security', 'Antibirus aurreratua 3 gailurako.', 3, 1, 5, 'Kaspersky', 'Softwarea', 'Berria', 49.95, 200, TRUE, 'https://images.unsplash.com/photo-1614064641938-3e852943702d?w=800&q=80'),
(70, 'Windows Server 2022', 'Zerbitzarietarako sistema estandarra.', 3, 2, 5, 'Microsoft', 'Softwarea', 'Berria', 800.00, 20, TRUE, 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800&q=80'),
(71, 'NordVPN 1 Year', 'Nabigazio segurua eta pribatua.', 3, 1, 5, 'NordSec', 'Softwarea', 'Berria', 59.00, 150, TRUE, 'https://images.unsplash.com/photo-1563206767-5b1d972b9fb1?w=800&q=80'),
(72, 'AutoCAD 2024', 'Ingeniaritza eta arkitektura diseinua.', 3, 2, 5, 'Autodesk', 'Softwarea', 'Berria', 1800.00, 5, TRUE, 'https://images.unsplash.com/photo-1581094794329-cd1361ddee2d?w=800&q=80'),
(73, 'Norton 360 Deluxe', 'Segurtasuna eta VPN integratua.', 3, 1, 5, 'Norton', 'Softwarea', 'Berria', 39.99, 80, TRUE, 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80'),
(74, 'Red Hat Enterprise Linux', 'Enpresen zerbitzarietarako Linux.', 3, 2, 5, 'Red Hat', 'Softwarea', 'Berria', 349.00, 10, TRUE, 'https://images.unsplash.com/photo-1629654297299-c8506221ca97?w=800&q=80'),
(75, 'VMware vSphere', 'Birtualizazio plataforma profesionala.', 3, 2, 5, 'VMware', 'Softwarea', 'Berria', 500.00, 8, TRUE, 'https://images.unsplash.com/photo-1551808525-51a94371425e?w=800&q=80');

INSERT INTO softwareak (id_produktua, software_mota, lizentzia_mota, bertsioa, garatzailea) VALUES
(66, 'Sistema Eragilea', 'Retail', '11 Pro', 'Microsoft'),
(67, 'Ofimatika', 'Retail', '2021', 'Microsoft'),
(68, 'Bestelakoak', 'Harpidetza', 'CC 2024', 'Adobe'),
(69, 'Antibirusa', 'Retail', '2024', 'Kaspersky'),
(70, 'Sistema Eragilea', 'OEM', 'Standard 2022', 'Microsoft'),
(71, 'Bestelakoak', 'Harpidetza', '1 Urte', 'NordSec'),
(72, 'Bestelakoak', 'Harpidetza', '2024', 'Autodesk'),
(73, 'Antibirusa', 'Retail', 'Deluxe', 'Norton'),
(74, 'Sistema Eragilea', 'Harpidetza', 'RHEL 9', 'Red Hat'),
(75, 'Bestelakoak', 'Retail', 'Standard 8', 'VMware');

-- ------------------------------------------------------------
-- 76-90: PERIFERIKOAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(76, 'Logitech MX Master 3S', 'Produktibitaterako sagu onena.', 2, 1, 4, 'Logitech', 'Periferikoak', 'Berria', 99.00, 40, TRUE, 'https://images.unsplash.com/photo-1615663245857-acda5b2b1518?w=800&q=80'),
(77, 'Keychron K2', 'Teklatu mekaniko trinkoa (Haririk gabea).', 2, 1, 4, 'Keychron', 'Periferikoak', 'Berria', 89.00, 20, TRUE, 'https://images.unsplash.com/photo-1587829745563-84b705c08d5d?w=800&q=80'),
(78, 'Razer BlackShark V2', 'Jokoetarako aurikularrak, soinu inguratzailea.', 1, 1, 4, 'Razer', 'Periferikoak', 'Berria', 99.00, 15, TRUE, 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=800&q=80'),
(79, 'Logitech C920', 'Webkamera estandarra streaming-erako.', 2, 1, 4, 'Logitech', 'Periferikoak', 'Berria', 75.00, 30, TRUE, 'https://images.unsplash.com/photo-1535295972055-1c762f4483e5?w=800&q=80'),
(80, 'Blue Yeti', 'Mikrofono profesionala USB bidez.', 1, 2, 4, 'Blue', 'Periferikoak', 'Berria', 120.00, 10, TRUE, 'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=800&q=80'),
(81, 'Corsair K70 RGB', 'Teklatu mekaniko azkarra jokoetarako.', 1, 2, 4, 'Corsair', 'Periferikoak', 'Berria', 160.00, 12, TRUE, 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=800&q=80'),
(82, 'Epson EcoTank', 'Tinta jaurtiketa inprimagailu ekonomikoa.', 2, 2, 4, 'Epson', 'Periferikoak', 'Berria', 250.00, 8, TRUE, 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=800&q=80'),
(83, 'Sony WH-1000XM5', 'Zarata deuseztatze onena merkatuan.', 1, 1, 4, 'Sony', 'Periferikoak', 'Berria', 349.00, 25, TRUE, 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=800&q=80'),
(84, 'Wacom Intuos', 'Tableta grafikoa marrazteko.', 2, 1, 4, 'Wacom', 'Periferikoak', 'Berria', 80.00, 15, TRUE, 'https://images.unsplash.com/photo-1563297924-f761d4b68426?w=800&q=80'),
(85, 'Elgato Stream Deck', 'Zuzenekoak kontrolatzeko botoi-panela.', 1, 2, 4, 'Elgato', 'Periferikoak', 'Berria', 149.00, 10, TRUE, 'https://images.unsplash.com/photo-1520697830682-bbb7e85e1b0b?w=800&q=80'),
(86, 'Brother Laser', 'Laser bidezko inprimagailu azkarra (Zuri/Beltz).', 2, 2, 4, 'Brother', 'Periferikoak', 'Berritua A', 120.00, 5, TRUE, 'https://images.unsplash.com/photo-1612815154858-60aa4c59eaa6?w=800&q=80'),
(87, 'JBL Charge 5', 'Bozgorailu eramangarria eta gogorra.', 2, 1, 4, 'JBL', 'Periferikoak', 'Berria', 150.00, 20, TRUE, 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=800&q=80'),
(88, 'Apple Magic Trackpad', 'Ukimen azalera handia Mac-erako.', 1, 1, 4, 'Apple', 'Periferikoak', 'Berria', 135.00, 10, TRUE, 'https://images.unsplash.com/photo-1616423640778-2cfd1dfeb8f0?w=800&q=80'),
(89, 'SteelSeries Apex Pro', 'Tekla magnetiko erregulagarriak.', 1, 2, 4, 'SteelSeries', 'Periferikoak', 'Berria', 200.00, 5, TRUE, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&q=80'),
(90, 'Canon Scanner', 'Dokumentuak digitalizatzeko eskanerra.', 2, 2, 4, 'Canon', 'Periferikoak', 'Berritua B', 60.00, 4, TRUE, 'https://images.unsplash.com/photo-1589492477829-5e65395b66cc?w=800&q=80');

INSERT INTO periferikoak (id_produktua, periferiko_mota, konexioa, ezaugarriak, argiztapena) VALUES
(76, 'Sagua', 'Haririk gabea', '8000 DPI, Scroll Infinitua', FALSE),
(77, 'Teklatua', 'Bluetooth', 'Mekanikoa, Gateron Brown', TRUE),
(78, 'Aurikularrak', 'Kablea (USB)', 'THX Audio, Mikrofonoa', FALSE),
(79, 'Webkamera', 'USB', '1080p, Estereo mikroa', TRUE),
(80, 'Bestelakoak', 'USB', 'Kondentsadorea, 4 modu', TRUE),
(81, 'Teklatua', 'Kablea (USB)', 'Cherry MX Red', TRUE),
(82, 'Inprimagailua', 'WiFi', 'Kolorea, Eskanerra', FALSE),
(83, 'Aurikularrak', 'Bluetooth', 'ANC, 30h bateria', FALSE),
(84, 'Bestelakoak', 'USB/BT', '4096 presio maila', FALSE),
(85, 'Bestelakoak', 'USB', '15 botoi LCD', TRUE),
(86, 'Inprimagailua', 'WiFi', 'Laser Monokromoa', FALSE),
(87, 'Bozgorailuak', 'Bluetooth', 'IP67, Powerbank', TRUE),
(88, 'Sagua', 'Bluetooth', 'Multitouch beira', FALSE),
(89, 'Teklatua', 'USB', 'OmniPoint Switchak', TRUE),
(90, 'Eskanerra', 'USB', 'A4, 300dpi', FALSE);

-- ------------------------------------------------------------
-- 91-100: KABLEAK
-- ------------------------------------------------------------
INSERT INTO produktuak (id_produktua, izena, deskribapena, hornitzaile_id, biltegi_id, kategoria_id, marka, mota, produktu_egoera, salmenta_prezioa, stock, salgai, irudia_url) VALUES
(91, 'HDMI 2.1 Kablea', '8K bideoa eta 120Hz euskarria.', 2, 1, 4, 'Ugreen', 'Kableak', 'Berria', 15.00, 100, TRUE, 'https://images.unsplash.com/photo-1544197150-b99a580bbcbf?w=800&q=80'),
(92, 'Ethernet CAT6 5m', 'Sare kablea abiadura handirako.', 2, 1, 4, 'Basics', 'Kableak', 'Berria', 8.50, 200, TRUE, 'https://images.unsplash.com/photo-1544197150-b99a580bbcbf?w=800&q=80'),
(93, 'USB-C to USB-C', '100W kargatze azkarra.', 1, 1, 4, 'Anker', 'Kableak', 'Berria', 12.00, 150, TRUE, 'https://images.unsplash.com/photo-1622737133809-d95047b9e673?w=800&q=80'),
(94, 'DisplayPort 1.4', 'Gaming pantailetarako aproposa.', 2, 2, 4, 'Ivanky', 'Kableak', 'Berria', 14.00, 80, TRUE, 'https://images.unsplash.com/photo-1544197150-b99a580bbcbf?w=800&q=80'),
(95, 'Lightning Kablea', 'iPhone kargatzeko (Ziurtatua).', 1, 1, 4, 'Apple', 'Kableak', 'Berria', 25.00, 60, TRUE, 'https://images.unsplash.com/photo-1600439614353-174ad0ee3b25?w=800&q=80'),
(96, 'SATA Datu Kablea', 'Disko gogorrak plakara konektatzeko.', 2, 2, 4, 'StarTech', 'Kableak', 'Berria', 3.00, 300, TRUE, 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?w=800&q=80'),
(97, 'Korronte Kablea EU', 'Ordenagailu iturrietarako estandarra.', 2, 1, 4, 'Generikoa', 'Kableak', 'Berria', 4.00, 200, TRUE, 'https://images.unsplash.com/photo-1555616635-640960031058?w=800&q=80'),
(98, 'VGA Kablea', 'Pantaila zaharrak konektatzeko.', 2, 2, 4, 'Generikoa', 'Kableak', 'Zehazteko', 5.00, 50, TRUE, 'https://images.unsplash.com/photo-1544197150-b99a580bbcbf?w=800&q=80'),
(99, 'Audio Jack 3.5mm', 'Auxiliar kablea soinurako.', 2, 1, 4, 'Ugreen', 'Kableak', 'Berria', 6.00, 120, TRUE, 'https://images.unsplash.com/photo-1555616635-640960031058?w=800&q=80'),
(100, 'USB A to Micro USB', 'Gailu zaharragoak kargatzeko.', 2, 2, 4, 'Basics', 'Kableak', 'Berria', 5.50, 100, TRUE, 'https://images.unsplash.com/photo-1622737133809-d95047b9e673?w=800&q=80');

INSERT INTO kableak (id_produktua, kable_mota, luzera_m, konektore_a, konektore_b, bertsioa) VALUES
(91, 'Bideoa', 2.0, 'HDMI Arra', 'HDMI Arra', '2.1'),
(92, 'Sarea', 5.0, 'RJ45', 'RJ45', 'CAT6'),
(93, 'Datuak', 1.0, 'USB-C', 'USB-C', '3.1 Gen2'),
(94, 'Bideoa', 2.0, 'DisplayPort', 'DisplayPort', '1.4'),
(95, 'Datuak', 1.0, 'USB-A', 'Lightning', 'MFi'),
(96, 'Barnekoak', 0.5, 'SATA', 'SATA', '3.0'),
(97, 'Korrontea', 1.5, 'Schuko', 'C13', 'Standard'),
(98, 'Bideoa', 1.8, 'VGA', 'VGA', 'Standard'),
(99, 'Audioa', 1.0, 'Jack 3.5', 'Jack 3.5', 'Stereo'),
(100, 'Datuak', 1.0, 'USB-A', 'Micro-USB', '2.0');



-- ============================================================
-- 5. TAILERRA ETA KONPONKETAK (DATUAK)
-- ============================================================

INSERT INTO akatsak (id_akatsa, izena, deskribapena) VALUES
(1, 'Pantaila hautsita', 'Kristala edo panela puskatuta dago.'),
(2, 'Bateria ez da kargatzen', 'Konektatzean ez du kargarik hartzen.'),
(3, 'Disko gogorraren errorea', 'Sistemak ez du diskoa antzematen edo zarata egiten du.'),
(4, 'Sistema eragilea ez da abiarazten', 'Blue Screen edo errorea abiaraztean.'),
(5, 'Teklatua ez dabil', 'Tekla batzuk edo denak ez dute erantzuten.'),
(6, 'Uraren kaltea', 'Likidoa isuri zaio gainean.'),
(7, 'Haizagailuaren zarata', 'Zarata handia edo ez dabil.'),
(8, 'Berotze arazoak', 'Ordenagailua itzali egiten da berotasunagatik.'),
(9, 'WiFi konexio arazoak', 'Ez du sarerik harrapatzen.'),
(10, 'Bluetooth errorea', 'Ez du gailurik aurkitzen.'),
(11, 'Kamera ez dabil', 'Webkamera beltza edo errorea.'),
(12, 'Audioa ez da entzuten', 'Ez dago soinurik bozgorailuetatik.'),
(13, 'Mikrofonoa ez dabil', 'Ez da soinurik grabatzen.'),
(14, 'USB portuak hondatuta', 'Ez dute gailurik antzematen.'),
(15, 'Kargagailuaren konektorea', 'Holgura du edo puskatuta dago.'),
(16, 'Birus edo Malwarea', 'Ordenagailua motel edo publizitatea.'),
(17, 'Datuak berreskuratu', 'Ezabatu edo galdu diren datuak.'),
(18, 'Pasahitza ahaztuta', 'Ezin da sisteman sartu.'),
(19, 'Pantaila keinuka', 'Irudia joan-etorrian dabil.'),
(20, 'Bateriaren iraupen motza', 'Oso azkar agortzen da.'),
(21, 'Trackpad ez dabil', 'Sagua ez da mugitzen.'),
(22, 'Bisagra hautsita', 'Pantaila ez da ondo ixten/irekitzen.'),
(23, 'Karkasa hondatuta', 'Kolpea edo pitzadura plastikoan.'),
(24, 'BIOS blokeatuta', 'Ezin da BIOSera sartu.'),
(25, 'RAM memoriaren errorea', 'Pitidoak abiaraztean.'),
(26, 'Txartel grafikoaren errorea', 'Artefaktuak pantailan.'),
(27, 'Sare txartela hondatuta', 'Ethernet ez dabil.'),
(28, 'Botoiak ez dabiltza', 'Pizteko edo bolumen botoiak.'),
(29, 'Sentsoreak ez dabiltza', 'Giro, hurbiltasun sentsoreak...'),
(30, 'Mantentze orokorra', 'Garbiketa eta pasta termikoa aldatzea.');

INSERT INTO konponketak (id_konponketa, produktua_id, langilea_id, hasiera_data, konponketa_egoera, akatsa_id, oharrak) VALUES
(1, 1, 8, '2023-01-10 09:00:00', 'Konponduta', 1, 'Pantaila berria jarri da.'),
(2, 5, 9, '2023-01-12 10:30:00', 'Prozesuan', 8, 'Pasta termikoa aldatu behar da.'),
(3, 12, 10, '2023-01-15 11:00:00', 'Prozesuan', 4, 'Diskoa formatu behar da.'),
(4, 22, 11, '2023-02-01 16:00:00', 'Konponduta', 2, 'Bateria aldatu da.'),
(5, 30, 8, '2023-02-05 09:15:00', 'Konponezina', 6, 'Plaka basea herdoilduta.'),
(6, 45, 9, '2023-02-10 12:00:00', 'Prozesuan', 15, 'Soldadura berria behar du.'),
(7, 55, 10, '2023-02-20 09:30:00', 'Konponduta', 11, 'Driver-ak eguneratu dira.'),
(8, 3, 11, '2023-03-01 10:00:00', 'Konponduta', 30, 'Haizagailuak garbitu dira.'),
(9, 8, 8, '2023-03-05 11:45:00', 'Prozesuan', 5, 'Teklatu osoa eskatu dugu.'),
(10, 15, 9, '2023-03-10 15:30:00', 'Konponduta', 16, 'Malwarebytes pasata.'),
(11, 25, 10, '2023-03-15 17:00:00', 'Prozesuan', 1, 'Aurrekontua bidali zaio bezeroari.'),
(12, 33, 11, '2023-04-01 09:00:00', 'Konponduta', 20, 'Bateria originala jarri da.'),
(13, 40, 8, '2023-04-05 10:30:00', 'Prozesuan', 7, 'Haizagailu berria bidean.'),
(14, 60, 9, '2023-04-10 11:00:00', 'Konponduta', 19, 'Kable flexa ondo konektatu da.'),
(15, 70, 10, '2023-04-15 14:00:00', 'Konponezina', 26, 'Grafikoa integratuta dago, plaka aldatu behar.'),
(16, 2, 11, '2023-05-01 16:30:00', 'Konponduta', 17, 'Recuva erabili dugu datuak ateratzeko.'),
(17, 6, 8, '2023-05-05 09:00:00', 'Prozesuan', 3, 'SSD berria probatu behar da.'),
(18, 9, 9, '2023-05-10 10:00:00', 'Prozesuan', 4, 'Windows berrinstalatzen.'),
(19, 18, 10, '2023-05-15 11:30:00', 'Konponduta', 14, 'USB plaka aldatu da.'),
(20, 28, 11, '2023-06-01 15:00:00', 'Konponduta', 22, 'Bisagra estutu da.'),
(21, 35, 8, '2023-06-05 16:00:00', 'Prozesuan', 23, 'Karkasa berria bilatzen.'),
(22, 42, 9, '2023-06-10 09:30:00', 'Konponduta', 28, 'Botoiaren flexa aldatu da.'),
(23, 50, 10, '2023-06-15 10:45:00', 'Konponduta', 12, 'Audio driverrak.'),
(24, 65, 11, '2023-07-01 12:00:00', 'Prozesuan', 9, 'WiFi txartela aldatzen.'),
(25, 75, 8, '2023-07-05 14:30:00', 'Konponduta', 29, 'Sentsorea kalibratu da.'),
(26, 10, 9, '2023-07-10 16:00:00', 'Konponduta', 2, 'Kargagailua zen arazoa, berria saldu zaio.'),
(27, 20, 10, '2023-07-15 09:00:00', 'Prozesuan', 1, 'Pantaila ez dago stock-ean.'),
(28, 38, 11, '2023-08-01 11:00:00', 'Konponduta', 13, 'Mikrofonoaren iragazkia garbitu.'),
(29, 48, 8, '2023-08-05 13:00:00', 'Konponduta', 25, 'RAM moduluak trukatu dira.'),
(30, 58, 9, '2023-08-10 15:15:00', 'Prozesuan', 18, 'Pasahitza reset egiten.');

-- ============================================================
-- 6. LOGISTIKA ETA SARRERAK (DATUAK)
-- ============================================================

/*-- Fitxaketak (Langileen sarrera/irteera erregistroa)
CREATE TABLE IF NOT EXISTS fitxaketak (
    id_fitxaketa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    langilea_id INT UNSIGNED NOT NULL,
    data DATE NOT NULL DEFAULT (CURRENT_DATE),
    ordua TIME NOT NULL DEFAULT (CURRENT_TIME),
    mota ENUM('Sarrera', 'Irteera') NOT NULL DEFAULT 'Sarrera',
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_fitxaketa_langilea FOREIGN KEY (langilea_id) REFERENCES langileak(id_langilea)
);*/

INSERT INTO fitxaketak (id_fitxaketa, langilea_id, data, ordua, mota) VALUES
(1, 1, '2024-01-08', '08:00:00', 'Sarrera'), (2, 1, '2024-01-08', '16:00:00', 'Irteera'),
(3, 2, '2024-01-08', '08:05:00', 'Sarrera'), (4, 2, '2024-01-08', '15:00:00', 'Irteera'),
(5, 3, '2024-01-08', '09:00:00', 'Sarrera'), (6, 3, '2024-01-08', '17:00:00', 'Irteera'),
(7, 4, '2024-01-08', '09:10:00', 'Sarrera'), (8, 4, '2024-01-08', '13:00:00', 'Irteera'),
(9, 5, '2024-01-08', '08:55:00', 'Sarrera'), (10, 5, '2024-01-08', '18:00:00', 'Irteera'),
(11, 6, '2024-01-08', '09:00:00', 'Sarrera'), (12, 6, '2024-01-08', '18:00:00', 'Irteera'),
(13, 7, '2024-01-08', '09:05:00', 'Sarrera'), (14, 7, '2024-01-08', '17:30:00', 'Irteera'),
(15, 8, '2024-01-08', '08:00:00', 'Sarrera'), (16, 8, '2024-01-08', '14:00:00', 'Irteera'),
(17, 9, '2024-01-08', '14:00:00', 'Sarrera'), (18, 9, '2024-01-08', '20:00:00', 'Irteera'),
(19, 10,'2024-01-08', '08:00:00', 'Sarrera'), (20, 10, '2024-01-08', '16:00:00', 'Irteera'),
(21, 11,'2024-01-08', '10:00:00', 'Sarrera'), (22, 11, '2024-01-08', '19:00:00', 'Irteera'),
(23, 12,'2024-01-08', '07:00:00', 'Sarrera'), (24, 12, '2024-01-08', '15:00:00', 'Irteera'),
(25, 13,'2024-01-08', '07:30:00', 'Sarrera'), (26, 13, '2024-01-08', '15:30:00', 'Irteera'),
(27, 14,'2024-01-08', '08:00:00', 'Sarrera'), (28, 14, '2024-01-08', '16:00:00', 'Irteera'),
(29, 1, '2024-01-09', '08:00:00', 'Sarrera'), (30, 1, '2024-01-09', '16:00:00', 'Irteera'),
(31, 5, '2024-01-09', '08:50:00', 'Sarrera'), (32, 5, '2024-01-09', '18:10:00', 'Irteera'),
(33, 8, '2024-01-09', '08:02:00', 'Sarrera'), (34, 8, '2024-01-09', '14:05:00', 'Irteera'),
(35, 12,'2024-01-09', '07:05:00', 'Sarrera'), (36, 12, '2024-01-09', '15:00:00', 'Irteera');

/* CREATE TABLE IF NOT EXISTS sarrerak (
    id_sarrera INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    data DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    hornitzailea_id INT UNSIGNED NOT NULL, 
    langilea_id INT UNSIGNED NOT NULL,     
    sarrera_egoera ENUM('Bidean', 'Jasota', "Ezabatua") NOT NULL DEFAULT 'Bidean',
    
    CONSTRAINT fk_sarrera_hornitzailea FOREIGN KEY (hornitzailea_id) REFERENCES hornitzaileak(id_hornitzailea),
    CONSTRAINT fk_sarrera_langilea FOREIGN KEY (langilea_id) REFERENCES langileak(id_langilea)
);*/

INSERT INTO sarrerak (id_sarrera, hornitzailea_id, langilea_id, sarrera_egoera, data) VALUES
(1, 1, 12, 'Jasota', '2023-11-01 09:00:00'),
(2, 2, 13, 'Jasota', '2023-11-02 10:00:00'),
(3, 3, 14, 'Bidean', '2023-11-03 11:30:00'),
(4, 1, 12, 'Jasota', '2023-11-05 08:15:00'),
(5, 2, 12, 'Jasota', '2023-11-10 14:00:00'),
(6, 1, 13, 'Ezabatua', '2023-11-12 16:30:00'),
(7, 3, 14, 'Jasota', '2023-11-15 09:45:00'),
(8, 2, 13, 'Bidean', '2023-11-18 10:00:00'),
(9, 1, 12, 'Jasota', '2023-11-20 11:00:00'),
(10, 3, 14, 'Jasota', '2023-11-25 15:30:00'),
(11, 2, 12, 'Jasota', '2023-12-01 09:10:00'),
(12, 1, 13, 'Jasota', '2023-12-03 10:20:00'),
(13, 1, 12, 'Bidean', '2023-12-05 12:00:00'),
(14, 2, 14, 'Jasota', '2023-12-08 14:45:00'),
(15, 3, 13, 'Jasota', '2023-12-10 09:30:00'),
(16, 1, 12, 'Jasota', '2023-12-12 16:00:00'),
(17, 2, 14, 'Jasota', '2023-12-15 11:15:00'),
(18, 3, 12, 'Bidean', '2023-12-18 08:30:00'),
(19, 1, 13, 'Ezabatua', '2023-12-20 13:45:00'),
(20, 2, 14, 'Jasota', '2023-12-22 10:00:00'),
(21, 3, 12, 'Jasota', '2023-12-24 09:00:00'),
(22, 1, 12, 'Jasota', '2023-12-28 15:00:00'),
(23, 1, 14, 'Jasota', '2024-01-03 11:30:00'),
(24, 2, 13, 'Bidean', '2024-01-05 14:00:00'),
(25, 3, 12, 'Jasota', '2024-01-08 09:15:00'),
(26, 1, 14, 'Jasota', '2024-01-10 10:45:00'),
(27, 2, 12, 'Jasota', '2024-01-12 16:00:00'),
(28, 3, 13, 'Jasota', '2024-01-15 08:30:00'),
(29, 1, 12, 'Bidean', '2024-01-18 13:20:00'),
(30, 2, 14, 'Jasota', '2024-01-20 11:00:00');

/*
CREATE TABLE IF NOT EXISTS sarrera_lerroak (
    id_sarrera_lerroa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sarrera_id INT UNSIGNED NOT NULL,
    produktua_id INT UNSIGNED NOT NULL,
    kantitatea  INT UNSIGNED NOT NULL, 
    sarrera_lerro_egoera ENUM('Bidean', 'Jasota', "Ezabatua") NOT NULL DEFAULT 'Bidean',
    CONSTRAINT fk_sl_sarrera FOREIGN KEY (sarrera_id) REFERENCES sarrerak(id_sarrera),
    CONSTRAINT fk_sl_produktua FOREIGN KEY (produktua_id) REFERENCES produktuak(id_produktua)
);
*/

INSERT INTO sarrera_lerroak (id_sarrera_lerroa, sarrera_id, produktua_id, kantitatea, sarrera_lerro_egoera) VALUES
(1, 1, 1, 10, 'Jasota'),
(2, 1, 2, 5, 'Jasota'),
(3, 2, 5, 20, 'Jasota'),
(4, 2, 10, 10, 'Jasota'),
(5, 3, 15, 5, 'Bidean'),
(6, 4, 20, 15, 'Jasota'),
(7, 5, 25, 8, 'Jasota'),
(8, 6, 30, 5, 'Ezabatua'),
(9, 7, 35, 12, 'Jasota'),
(10, 7, 40, 4, 'Jasota'),
(11, 8, 45, 10, 'Bidean'),
(12, 9, 50, 2, 'Jasota'),
(13, 10, 55, 6, 'Jasota'),
(14, 11, 60, 20, 'Jasota'),
(15, 12, 65, 5, 'Jasota'),
(16, 13, 70, 15, 'Bidean'),
(17, 14, 75, 4, 'Jasota'),
(18, 15, 80, 10, 'Jasota'),
(19, 16, 85, 8, 'Jasota'),
(20, 17, 90, 50, 'Jasota'),
(21, 18, 91, 100, 'Bidean'),
(22, 19, 95, 20, 'Ezabatua'),
(23, 20, 5, 10, 'Jasota'),
(24, 21, 15, 5, 'Jasota'),
(25, 22, 25, 8, 'Jasota'),
(26, 23, 35, 12, 'Jasota'),
(27, 24, 45, 10, 'Bidean'),
(28, 25, 55, 6, 'Jasota'),
(29, 26, 65, 5, 'Jasota'),
(30, 27, 75, 4, 'Jasota'),
(31, 28, 85, 8, 'Jasota'),
(32, 29, 95, 20, 'Bidean'),
(33, 30, 5, 10, 'Jasota');

-- ============================================================
-- 7. SALMENTAK ETA FAKTURAZIOA (DATUAK)
-- ============================================================

/*CREATE TABLE IF NOT EXISTS eskaerak (
    id_eskaera INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bezeroa_id INT UNSIGNED NOT NULL, 
    langilea_id INT UNSIGNED, 
    data DATETIME DEFAULT CURRENT_TIMESTAMP,
    eguneratze_data DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    guztira_prezioa DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    
    eskaera_egoera ENUM('Prestatzen', 'Osatua/Bidalita', "Ezabatua") NOT NULL DEFAULT 'Prestatzen',
    
    CONSTRAINT fk_eskaera_bezeroa FOREIGN KEY (bezeroa_id) REFERENCES bezeroak(id_bezeroa),
    CONSTRAINT fk_eskaera_langilea FOREIGN KEY (langilea_id) REFERENCES langileak(id_langilea)
);*/

INSERT INTO eskaerak (id_eskaera, bezeroa_id, langilea_id, guztira_prezioa, eskaera_egoera, data) VALUES
(1, 1, 5, 2199.00, 'Osatua/Bidalita', '2024-01-08 10:00:00'),
(2, 2, 6, 1499.00, 'Osatua/Bidalita', '2024-01-09 11:30:00'),
(3, 1, 7, 799.00, 'Prestatzen', '2024-01-10 09:15:00'),
(4, 3, 5, 4500.00, 'Osatua/Bidalita', '2024-01-11 14:00:00'),
(5, 4, 6, 1209.00, 'Prestatzen', '2024-01-12 16:45:00'),
(6, 2, 7, 299.00, 'Osatua/Bidalita', '2024-01-14 08:30:00'),
(7, 5, 5, 150.00, 'Ezabatua', '2024-01-15 10:00:00'),
(8, 1, 6, 99.00, 'Osatua/Bidalita', '2024-01-18 13:20:00'),
(9, 3, 7, 3800.00, 'Osatua/Bidalita', '2024-01-22 09:10:00'),
(10, 4, 5, 600.00, 'Osatua/Bidalita', '2024-01-25 15:40:00'),
(11, 2, 6, 135.00, 'Prestatzen', '2024-01-28 11:00:00'),
(12, 1, 7, 450.00, 'Osatua/Bidalita', '2024-02-01 10:30:00'),
(13, 3, 5, 2500.00, 'Osatua/Bidalita', '2024-02-05 14:15:00'),
(14, 5, 6, 89.00, 'Prestatzen', '2024-02-08 09:00:00'),
(15, 2, 7, 120.00, 'Osatua/Bidalita', '2024-02-12 16:20:00'),
(16, 4, 5, 2499.00, 'Osatua/Bidalita', '2024-02-15 11:00:00'),
(17, 1, 6, 1400.00, 'Prestatzen', '2024-02-18 13:45:00'),
(18, 3, 7, 550.00, 'Osatua/Bidalita', '2024-02-22 10:15:00'),
(19, 2, 5, 15.00, 'Osatua/Bidalita', '2024-02-25 09:30:00'),
(20, 5, 6, 45.00, 'Ezabatua', '2024-03-01 15:00:00'),
(21, 1, 7, 1250.00, 'Osatua/Bidalita', '2024-03-05 14:30:00'),
(22, 4, 5, 900.00, 'Prestatzen', '2024-03-08 11:20:00'),
(23, 2, 6, 320.00, 'Osatua/Bidalita', '2024-03-12 09:45:00'),
(24, 3, 7, 1800.00, 'Osatua/Bidalita', '2024-03-15 16:10:00'),
(25, 1, 5, 650.00, 'Prestatzen', '2024-03-18 10:00:00'),
(26, 5, 6, 75.00, 'Osatua/Bidalita', '2024-03-22 13:50:00'),
(27, 2, 7, 1500.00, 'Osatua/Bidalita', '2024-03-25 11:15:00'),
(28, 3, 5, 2200.00, 'Prestatzen', '2024-03-28 15:30:00'),
(29, 4, 6, 60.00, 'Osatua/Bidalita', '2024-04-01 09:20:00'),
(30, 1, 7, 1200.00, 'Osatua/Bidalita', '2024-04-05 14:00:00');

/* CREATE TABLE IF NOT EXISTS eskaera_lerroak (
    id_eskaera_lerroa INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eskaera_id INT UNSIGNED NOT NULL, 
    produktua_id INT UNSIGNED NOT NULL, 
    kantitatea INT UNSIGNED NOT NULL, 
    unitate_prezioa DECIMAL(10, 2) NOT NULL, 
    
    
    eskaera_lerro_egoera ENUM('Prestatzen', 'Osatua/Bidalita', "Ezabatua") NOT NULL DEFAULT 'Prestatzen',
    
    
    
    CONSTRAINT fk_el_eskaera FOREIGN KEY (eskaera_id) REFERENCES eskaerak(id_eskaera),
    CONSTRAINT fk_el_produktua FOREIGN KEY (produktua_id) REFERENCES produktuak(id_produktua)
    
);
*/

INSERT INTO eskaera_lerroak (id_eskaera_lerroa, eskaera_id, produktua_id, kantitatea, unitate_prezioa, eskaera_lerro_egoera) VALUES
(1, 1, 1, 1, 2199.00, 'Osatua/Bidalita'),
(2, 2, 2, 1, 1499.00, 'Osatua/Bidalita'),
(3, 3, 24, 1, 799.00, 'Prestatzen'),
(4, 4, 46, 1, 4500.00, 'Osatua/Bidalita'),
(5, 5, 21, 1, 1209.00, 'Prestatzen'),
(6, 6, 51, 1, 299.00, 'Osatua/Bidalita'),
(7, 7, 87, 1, 150.00, 'Ezabatua'),
(8, 8, 76, 1, 99.00, 'Osatua/Bidalita'),
(9, 9, 47, 1, 3800.00, 'Osatua/Bidalita'),
(10, 10, 32, 1, 600.00, 'Osatua/Bidalita'),
(11, 11, 88, 1, 135.00, 'Prestatzen'),
(12, 12, 29, 1, 450.00, 'Osatua/Bidalita'),
(13, 13, 11, 1, 2499.00, 'Osatua/Bidalita'),
(14, 14, 77, 1, 89.00, 'Prestatzen'),
(15, 15, 80, 1, 120.00, 'Osatua/Bidalita'),
(16, 16, 11, 1, 2499.00, 'Osatua/Bidalita'),
(17, 17, 2, 1, 1400.00, 'Prestatzen'), 
(18, 18, 61, 1, 550.00, 'Osatua/Bidalita'),
(19, 19, 91, 1, 15.00, 'Osatua/Bidalita'),
(20, 20, 95, 1, 25.00, 'Ezabatua'),
(21, 20, 92, 2, 10.00, 'Ezabatua'), 
(22, 21, 4, 1, 950.00, 'Osatua/Bidalita'),
(23, 21, 54, 1, 300.00, 'Osatua/Bidalita'),
(24, 22, 34, 1, 900.00, 'Prestatzen'),
(25, 23, 62, 1, 320.00, 'Osatua/Bidalita'),
(26, 24, 17, 1, 1800.00, 'Osatua/Bidalita'),
(27, 25, 27, 1, 650.00, 'Prestatzen'),
(28, 26, 79, 1, 75.00, 'Osatua/Bidalita'),
(29, 27, 49, 1, 1500.00, 'Osatua/Bidalita'),
(30, 28, 48, 1, 2200.00, 'Prestatzen'),
(31, 29, 90, 1, 60.00, 'Osatua/Bidalita'),
(32, 30, 50, 1, 1200.00, 'Osatua/Bidalita');

/*CREATE TABLE IF NOT EXISTS bezero_fakturak (
    id_faktura INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    faktura_zenbakia VARCHAR(20) UNIQUE NOT NULL,     
    eskaera_id INT UNSIGNED UNIQUE NOT NULL,   -- bezeroaren datuak hemendik aterako dira
    data DATE DEFAULT (CURRENT_DATE) NOT NULL,
    
    zergak_ehunekoa DECIMAL(5, 2) DEFAULT 21.00 NOT NULL, 
    fitxategia_url VARCHAR(255), 
    --
    CONSTRAINT fk_faktura_eskaera FOREIGN KEY (eskaera_id) REFERENCES eskaerak(id_eskaera)
);*/

INSERT INTO bezero_fakturak (id_faktura, faktura_zenbakia, eskaera_id, data, zergak_ehunekoa) VALUES
(1, 'FAK-2024-001', 1, '2024-01-10', 21.00),
(2, 'FAK-2024-002', 2, '2024-01-11', 21.00),
(3, 'FAK-2024-003', 4, '2024-01-12', 21.00),
(4, 'FAK-2024-004', 6, '2024-01-15', 21.00),
(5, 'FAK-2024-005', 8, '2024-01-20', 21.00),
(6, 'FAK-2024-006', 9, '2024-01-25', 21.00),
(7, 'FAK-2024-007', 10, '2024-02-01', 21.00),
(8, 'FAK-2024-008', 12, '2024-02-05', 21.00),
(9, 'FAK-2024-009', 13, '2024-02-10', 21.00),
(10, 'FAK-2024-010', 15, '2024-02-15', 21.00),
(11, 'FAK-2024-011', 16, '2024-02-20', 21.00),
(12, 'FAK-2024-012', 18, '2024-03-01', 21.00),
(13, 'FAK-2024-013', 19, '2024-03-05', 21.00),
(14, 'FAK-2024-014', 21, '2024-03-10', 21.00),
(15, 'FAK-2024-015', 23, '2024-03-15', 21.00),
(16, 'FAK-2024-016', 24, '2024-03-20', 21.00),
(17, 'FAK-2024-017', 26, '2024-04-01', 21.00),
(18, 'FAK-2024-018', 27, '2024-04-05', 21.00),
(19, 'FAK-2024-019', 29, '2024-04-10', 21.00),
(20, 'FAK-2024-020', 30, '2024-04-15', 21.00);

-- ========================================================================================================================================================================
-- ========================================================================================================================================================================
-- ========================================================
-- 5. ERABILTZAILEAK ETA BAIMENAK (BIRTEK DB)
-- ========================================================
-- OHARRA: Pasahitza "1234" da erabiltzaile GUZTIENTZAT probak egiteko.
-- ========================================================

USE birtek_db;
FLUSH PRIVILEGES;

-- --------------------------------------------------------
-- 1. ZUZENDARITZA (SysAdmin) - Ander Urien, Lander Garmendia
-- --------------------------------------------------------
CREATE USER IF NOT EXISTS 'ander_sysadmin'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON *.* TO 'ander_sysadmin'@'localhost' WITH GRANT OPTION;

CREATE USER IF NOT EXISTS 'lander_sysadmin'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON *.* TO 'lander_sysadmin'@'localhost' WITH GRANT OPTION;

-- --------------------------------------------------------
-- 2. ADMINISTRAZIOA - Ane Lasa, Mikel Otegi
-- --------------------------------------------------------
CREATE USER IF NOT EXISTS 'ane_admin'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'mikel_admin'@'localhost' IDENTIFIED BY '1234';

-- Baimenak esleitu bi erabiltzaileei (Admin osoa + Fitxaketak)
GRANT SELECT, INSERT, UPDATE, DELETE ON birtek_db.kontratu_motak TO     'ane_admin'@'localhost', 'mikel_admin'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON birtek_db.langileak TO          'ane_admin'@'localhost', 'mikel_admin'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON birtek_db.langile_sailak TO     'ane_admin'@'localhost', 'mikel_admin'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON birtek_db.fitxaketak TO         'ane_admin'@'localhost', 'mikel_admin'@'localhost';
GRANT SELECT, INSERT, UPDATE ON birtek_db.bezero_fakturak TO            'ane_admin'@'localhost', 'mikel_admin'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON birtek_db.hornitzaileak TO      'ane_admin'@'localhost', 'mikel_admin'@'localhost';

-- --------------------------------------------------------
-- 3. SALMENTAK - Leire, Iker, Amaia
-- --------------------------------------------------------
CREATE USER IF NOT EXISTS 'leire_sales'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'iker_sales'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'amaia_sales'@'localhost' IDENTIFIED BY '1234';

-- Baimenak esleitu (Bezeroak, Produktuak Eskaerak, Eskaera Lerroak, fakturak)
GRANT SELECT, INSERT, UPDATE, DELETE ON birtek_db.bezeroak TO   'leire_sales'@'localhost', 'iker_sales'@'localhost', 'amaia_sales'@'localhost';
GRANT SELECT ON birtek_db.produktuak TO                         'leire_sales'@'localhost', 'iker_sales'@'localhost', 'amaia_sales'@'localhost';
GRANT SELECT, INSERT, UPDATE ON birtek_db.eskaerak TO           'leire_sales'@'localhost', 'iker_sales'@'localhost', 'amaia_sales'@'localhost';
GRANT SELECT, INSERT, UPDATE ON birtek_db.eskaera_lerroak TO    'leire_sales'@'localhost', 'iker_sales'@'localhost', 'amaia_sales'@'localhost';
GRANT SELECT, INSERT, UPDATE ON birtek_db.bezero_fakturak TO    'leire_sales'@'localhost', 'iker_sales'@'localhost', 'amaia_sales'@'localhost';

-- --------------------------------------------------------
-- 4. SAT (ZERBITZU TEKNIKOA) - Unai, Maite, Aitor, Nerea
-- --------------------------------------------------------
CREATE USER IF NOT EXISTS 'unai_sat'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'maite_sat'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'aitor_sat'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'nerea_sat'@'localhost' IDENTIFIED BY '1234';

-- Baimenak esleitu (Tailerra: Produktuak, Konponketak, Akatsak)
GRANT SELECT, UPDATE ON birtek_db.produktuak TO                 'unai_sat'@'localhost', 'maite_sat'@'localhost', 'aitor_sat'@'localhost', 'nerea_sat'@'localhost';
GRANT SELECT, INSERT, UPDATE ON birtek_db.konponketak TO        'unai_sat'@'localhost', 'maite_sat'@'localhost', 'aitor_sat'@'localhost', 'nerea_sat'@'localhost';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON birtek_db.konponketa_lerroak TO 'unai_sat'@'localhost', 'maite_sat'@'localhost', 'aitor_sat'@'localhost', 'nerea_sat'@'localhost';
GRANT SELECT, INSERT, UPDATE ON birtek_db.akatsak TO            'unai_sat'@'localhost', 'maite_sat'@'localhost', 'aitor_sat'@'localhost', 'nerea_sat'@'localhost';


-- --------------------------------------------------------
-- 5. LOGISTIKA / BILTEGIA - Gorka, Oihane, Xabier
-- --------------------------------------------------------
CREATE USER IF NOT EXISTS 'gorka_biltegia'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'oihane_biltegia'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'xabier_biltegia'@'localhost' IDENTIFIED BY '1234';

-- Baimenak esleitu (Sarrerak eta Stocka)
GRANT SELECT, INSERT, UPDATE ON birtek_db.produktuak TO     'gorka_biltegia'@'localhost', 'oihane_biltegia'@'localhost', 'xabier_biltegia'@'localhost';
GRANT SELECT ON birtek_db.biltegiak TO                      'gorka_biltegia'@'localhost', 'oihane_biltegia'@'localhost', 'xabier_biltegia'@'localhost';
GRANT SELECT, UPDATE ON birtek_db.sarrera_lerroak TO        'gorka_biltegia'@'localhost', 'oihane_biltegia'@'localhost', 'xabier_biltegia'@'localhost';
GRANT SELECT, UPDATE ON birtek_db.sarrerak TO               'gorka_biltegia'@'localhost', 'oihane_biltegia'@'localhost', 'xabier_biltegia'@'localhost';


GRANT SELECT, UPDATE ON birtek_db.eskaera_lerroak TO        'gorka_biltegia'@'localhost', 'oihane_biltegia'@'localhost', 'xabier_biltegia'@'localhost';

-- ========================================================
-- 6. FITXAKETAK (Fitxaketak egiteko baimenak)
-- ========================================================
-- Langile GUZTIEK beraien sarrera/irteerak erregistratu (INSERT) eta ikusi (SELECT) behar dituzte.
-- OHARRA: Admin erabiltzaileek (Ane, Mikel) jada badute baimen hau (eta UPDATE/DELETE ere bai).
-- SysAdmin erabiltzaileek (Ander, Lander) dena egin dezakete.
-- Beraz, hemen Salmentak, SAT eta Biltegiko langileei emango diegu baimena:

GRANT SELECT, INSERT ON birtek_db.fitxaketak TO 
    'leire_sales'@'localhost', 
    'iker_sales'@'localhost', 
    'amaia_sales'@'localhost', 
    'unai_sat'@'localhost', 
    'maite_sat'@'localhost', 
    'aitor_sat'@'localhost', 
    'nerea_sat'@'localhost', 
    'gorka_biltegia'@'localhost', 
    'oihane_biltegia'@'localhost', 
    'xabier_biltegia'@'localhost';

FLUSH PRIVILEGES;