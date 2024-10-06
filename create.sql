USE piattaforma_gare;

CREATE TABLE Gara (
    Nome VARCHAR(50) PRIMARY KEY,
    Data DATE,
    Durata TIME,
    Tipo VARCHAR(50),
    Nome_Circuito VARCHAR(50),
    FOREIGN KEY (Nome_Circuito) REFERENCES Circuito(Nome)
);

CREATE TABLE Circuito (
    Nome VARCHAR(50) PRIMARY KEY,
    Paese VARCHAR(50),
    Num_Curve INT,
    Lunghezza DECIMAL(8,2),
    CHECK (Num_Curve >= 0),
    CHECK (Lunghezza >= 0)
);

CREATE TABLE Partecipazione (
    Vettura INT,
    Gara VARCHAR(50),
    Punti_Guadagnati INT,
    Motivo_Ritiro VARCHAR(50),
    PRIMARY KEY (Vettura, Gara),
    FOREIGN KEY (Vettura) REFERENCES Vettura(Numero_Gara),
    FOREIGN KEY (Gara) REFERENCES Gara(Nome),
    CHECK (Motivo_Ritiro IN ('Incidente', 'Guasto Meccanico', 'Squalifica'))
);

CREATE TABLE Vettura (
    Numero_Gara INT PRIMARY KEY,
    Modello VARCHAR(50)
);

CREATE TABLE Responsabilita (
    Vettura INT,
    Scuderia VARCHAR(50),
    PRIMARY KEY (Vettura, Scuderia),
    FOREIGN KEY (Vettura) REFERENCES Vettura(Numero_Gara),
    FOREIGN KEY (Scuderia) REFERENCES Scuderia(Nome)
);

CREATE TABLE Scuderia (
    Nome VARCHAR(50) PRIMARY KEY,
    Paese_Sede VARCHAR(50)
);

CREATE TABLE Finanziamento (
    Pilota VARCHAR(50),
    Scuderia VARCHAR(50),
    Codice_Finanziamento INT PRIMARY KEY,
    Quota DECIMAL(8,2),
    FOREIGN KEY (Pilota) REFERENCES Pilota(Cognome),
    FOREIGN KEY (Scuderia) REFERENCES Scuderia(Nome)
);

CREATE TABLE Pilota (
    Nome VARCHAR(50),
    Cognome VARCHAR(50),
    Codice_Pilota INT PRIMARY KEY,
    Data_Nascita DATE,
    Nazionalita VARCHAR(50)
);

CREATE TABLE Tipologia_Pilota (
    Pilota VARCHAR(50) PRIMARY KEY,
    Licenze INT,
    Data_Prima_Licenza DATE,
    Tipo VARCHAR(50) CHECK (Tipo IN ('Pilota AM', 'Pilota PRO', 'Gentleman Driver')),
    FOREIGN KEY (Pilota) REFERENCES Pilota(Cognome)
);

CREATE TABLE Composizione (
    Pilota VARCHAR(50),
    Equipaggio INT,
    PRIMARY KEY (Pilota, Equipaggio),
    FOREIGN KEY (Pilota) REFERENCES Pilota(Cognome),
    FOREIGN KEY (Equipaggio) REFERENCES Equipaggio(Numero_Equipaggio)
);

CREATE TABLE Equipaggio (
    Numero_Equipaggio INT PRIMARY KEY,
    Num_Piloti INT CHECK (Num_Piloti > 0)
);

CREATE TABLE Guida (
    Equipaggio INT,
    Vettura INT,
    PRIMARY KEY (Equipaggio, Vettura),
    FOREIGN KEY (Equipaggio) REFERENCES Equipaggio(Numero_Equipaggio),
    FOREIGN KEY (Vettura) REFERENCES Vettura(Numero_Gara)
);

CREATE TABLE Assemblaggio (
    Vettura INT,
    Componente INT,
    Data_Installazione DATE,
    PRIMARY KEY (Vettura, Componente),
    FOREIGN KEY (Vettura) REFERENCES Vettura(Numero_Gara),
    FOREIGN KEY (Componente) REFERENCES Componente(Codice)
);

CREATE TABLE Componente (
    Codice INT PRIMARY KEY,
    Costo DECIMAL(8,2),
    Nome_Costruttore VARCHAR(50)
);

CREATE TABLE Costruttore (
    Nome VARCHAR(50) PRIMARY KEY,
    Sede VARCHAR(50),
    Regione_Sociale VARCHAR(50)
);

CREATE TABLE Motore (
    Componente INT PRIMARY KEY,
    Cilindri INT,
    Cilindrata DECIMAL(8,2),
    Tipo VARCHAR(50) CHECK (Tipo IN ('Turbo', 'Aspirato')),
    FOREIGN KEY (Componente) REFERENCES Componente(Codice),
    CHECK (Cilindri > 0),
    CHECK (Cilindrata >= 0)
);

CREATE TABLE Telaio (
    Componente INT PRIMARY KEY,
    Materiale VARCHAR(50),
    FOREIGN KEY (Componente) REFERENCES Componente(Codice)
);

CREATE TABLE Cambio (
    Componente INT PRIMARY KEY,
    Marce INT CHECK (Marce IN (7, 8)),
    FOREIGN KEY (Componente) REFERENCES Componente(Codice)
);