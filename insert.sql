USE piattaforme_gare;

-- Inserimento dati nella tabella Circuito
INSERT INTO Circuito (Nome, Paese, Num_Curve, Lunghezza) 
VALUES ('Monza', 'Italia', 11, 5.793);

-- Inserimento dati nella tabella Gara
INSERT INTO Gara (Nome, Data, Durata, Tipo, Nome_Circuito) 
VALUES ('Gara1', '2023-01-01', '03:30:00', 'Corsa', 'Monza');

-- Inserimento dati nella tabella Vettura
INSERT INTO Vettura (Numero_Gara, Modello) 
VALUES (1, 'Ferrari');

INSERT INTO Vettura (Numero_Gara, Modello) 
VALUES (2, 'Aston');

-- Inserimento dati nella tabella Scuderia
INSERT INTO Scuderia (Nome, Paese_Sede) 
VALUES ('Scuderia Ferrari', 'Italia');

INSERT INTO Scuderia(Nome, Paese_Sede)
VALUES ('Aston Martin', 'Inghilterra');

-- Inserimento dati nella tabella Responsabilita
INSERT INTO Responsabilita (Vettura, Scuderia) 
VALUES (1, 'Scuderia Ferrari');

-- Inserimento dati nella tabella Pilota
INSERT INTO Pilota (Nome, Cognome, Codice_Pilota, Data_Nascita, Nazionalita) 
VALUES ('Sebastian', 'Vettel', 1, '1987-07-03', 'Germania');

INSERT INTO Pilota (Nome,Cognome, Codice_Pilota, Data_Nascita, Nazionalita)
VALUES ('Alfio', 'Marra', 2, '19-05-2001', 'Italia'); -- esempio di Gentleam Driver

-- Inserimento dati nella tabella Finanziamento
INSERT INTO Finanziamento (Pilota, Scuderia, Codice_Finanziamento, Quota) 
VALUES ('Alfio Marra', 'Aston Martin', 1, 5000000.00);

-- Inserimento dati nella tabella Tipologia_Pilota
INSERT INTO Tipologia_Pilota (Pilota, Licenze, Data_Prima_Licenza, Tipo) 
VALUES ('Vettel', 3, '2005-01-01', 'pilota PRO');

INSERT INTO Tipologia_Pilota (Pilota, Licenze, Data_Prima_Licenza, Tipo) 
VALUES('Marra', 0, NULL,'Gentleman Driver');

-- Inserimento dati nella tabella Composizione
INSERT INTO Composizione (Pilota, Equipaggio) 
VALUES ('Sebastian Vettel', 1); -- Vettel fa parte dell'equipaggio #1

INSERT INTO Composizione (Pilota, Equipaggio)
VALUES('Marra',2); -- Marra fa parte dell'equipaggio #2

-- Inserimento dati nella tabella Equipaggio
INSERT INTO Equipaggio (Numero_Equipaggio, Num_Piloti) 
VALUES (1, 1); -- l'equipaggio #1 possiede 1 pilota

INSERT INTO Equipaggio(Numero_Equipaggio,Num_Piloti)
VALUES (2,1);

-- Inserimento dati nella tabella Guida
INSERT INTO Guida (Equipaggio, Vettura) 
VALUES (1, 1); -- l'equipaggio #1 guida la vettura #1

INSERT INTO Guida(Equipaggio, Vettura)
VALUES (2, 2); -- l'equipaggio #2 guida la vettura #2


-- Inserimento dati nella tabella Assemblaggio
INSERT INTO Assemblaggio (Vettura, Componente, Data_Installazione) 
VALUES (1, 1, '2023-01-01');

-- Inserimento dati nella tabella Componente
-- Componenti vettura #1
INSERT INTO Componente (Codice, Costo, Nome_Costruttore) 
VALUES (1, 1000.00, 'Costruttore1');
INSERT INTO Componente(Codice, Costo, Nome_Costruttore)
VALUES (2, 5000.00,'Costruttore1');
INSERT INTO Componente(Codice, Costo, Nome_Costruttore)
VALUES (3,7000.00,'Costruttore1');

-- Componenti vettura #2
INSERT INTO Componente(Codice, Costo, Nome_Costruttore)
VALUES (4,1500.00,'Costruttore2');
INSERT INTO Componente(Codice, Costo, Nome_Costruttore)
VALUES (5, 7500.00, 'Costruttore2');
INSERT INTO Componente(Codice, Costo, Nome_Costruttore)
VALUES (6, 3500.00, 'Costruttore2');

-- Inserimento dati nella tabella Costruttore
INSERT INTO Costruttore (Nome, Sede, Regione_Sociale) 
VALUES ('Costruttore1', 'Sede1', 'Italia');

INSERT INTO Costruttore(Nome, Sede, Reagione_Sociale)
VALUES('Costruttore2', 'Sede2', 'Inghilterra');

-- Inserimento dati nella tabella Motore
INSERT INTO Motore (Componente, Cilindri, Cilindrata, Tipo) 
VALUES (1, 6, 3000.00, 'Turbo');

-- Inserimento dati nella tabella Telaio
INSERT INTO Telaio (Componente, Materiale) 
VALUES (2, 'Alluminio');

-- Inserimento dati nella tabella Cambio
INSERT INTO Cambio (Componente, Marce) 
VALUES (3, 7);


-- Inserimento dati macchina #2
INSERT INTO Motore(Componente, Cilindri, Cilindrata, Tipo)
VALUES(4, 8, 4000.00, 'Aspirato');

INSERT INTO Telaio(Componente, Materiale)
VALUES(5, 'Fibra di Carbonio');

INSERT INTO Cambio(Componente, Marce)
VALUES (6, 8);

-- Inserimento Finanziamento
INSERT INTO Finanzimento(Pilota, Scuderia, Codice_Finanziamento, Quota)
VALUES ('Marra','Scuderia Ferrari','5454','1000');

-- Inseriemento Partecipazione (tutti gli attributi)
INSERT INTO Partecipazione(Vettura,Gara,Punti_Guadagnati,Motivo_Ritiro)
VALUES ('5','Gara1','0','Incidente');

-- inserimento Partecipazione (alcuni attributi)
INSERT INTO Partecipazione(Vettura,Gara,Punti_Guadagnati)
VALUES ('1','Gara1','3');
