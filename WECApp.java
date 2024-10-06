import java.sql.*;

public class WECApp{

    //METODO PER LA CONNESSIONE AL DRIVER E AL DATABASE
    private static Connection getConnection() throws SQLException { //classe di gestione della connessione al database
        String url = "jdbc:mysql://localhost:3306/Piattaforma_GestioneGare";
        String username = "your_username";
        String password = "your_password";
        return DriverManager.getConnection(url, username, password);
    }

    //OPERAZIONE 1
    private static void registrazioneScuderia(String nome, String paeseSede) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Scuderia (Nome, Paese_Sede) VALUES (?, ?)")) {
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, paeseSede);
            preparedStatement.executeUpdate();
            System.out.println("Scuderia registrata con successo.");
        }
    }

    //OPERAZIONE 2 
    private static void inserimentoVettura(String modello, String scuderia, String tipoMotore, String materialeTelaio, int marceCambio) throws SQLException {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
    
            try (PreparedStatement vetturaStatement = connection.prepareStatement("INSERT INTO Vettura (Modello, Scuderia) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                vetturaStatement.setString(1, modello);
                vetturaStatement.setString(2, scuderia);
                vetturaStatement.executeUpdate();
    
                ResultSet generatedKeys = vetturaStatement.getGeneratedKeys();
                int numeroGara = -1;
                if (generatedKeys.next()) {
                    numeroGara = generatedKeys.getInt(1);
                }
    
                // Inserimento Componente Telaio
                int codiceTelaio = inserisciComponente(connection, "Componente Telaio", 10000.0, "CostruttoreX");
    
                // RV1: Verifica Montaggio Componente Telaio
                if (verificaMontaggioComponente(numeroGara, codiceTelaio)) {
                    // Inserimento Assemblaggio Componente Telaio
                    inserisciAssemblaggio(connection, numeroGara, codiceTelaio);
    
                    // Inserimento Componente Motore
                    int codiceMotore = inserisciComponente(connection, "Componente Motore", 15000.0, "CostruttoreY");
    
                    // RV1: Verifica Montaggio Componente Motore
                    if (verificaMontaggioComponente(numeroGara, codiceMotore)) {
                        // Inserimento Assemblaggio Componente Motore
                        inserisciAssemblaggio(connection, numeroGara, codiceMotore);
    
                        // Inserimento Componente Cambio
                        int codiceCambio = inserisciComponente(connection, "Componente Cambio", 8000.0, "CostruttoreZ");
    
                        // RV1: Verifica Montaggio Componente Cambio
                        if (verificaMontaggioComponente(numeroGara, codiceCambio)) {
                            // Inserimento Assemblaggio Componente Cambio
                            inserisciAssemblaggio(connection, numeroGara, codiceCambio);
                            connection.commit();
                            System.out.println("Vettura e Componenti inseriti con successo.");
                        } else {
                            System.out.println("Impossibile montare due componenti dello stesso tipo sulla stessa vettura."); //implementazione del requistito non funzionale RV1
                        }
                    } else {
                        System.out.println("Impossibile montare due componenti dello stesso tipo sulla stessa vettura.");
                    }
                } else {
                    System.out.println("Impossibile montare due componenti dello stesso tipo sulla stessa vettura.");
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
    
    private static int inserisciComponente(Connection connection, String nomeComponente, double costo, String nomeCostruttore) throws SQLException { //serve per aggiungere un componente al database
        try (PreparedStatement componenteStatement = connection.prepareStatement("INSERT INTO Componente (Nome, Costo, Nome_Costruttore) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            componenteStatement.setString(1, nomeComponente);
            componenteStatement.setDouble(2, costo);
            componenteStatement.setString(3, nomeCostruttore);
            componenteStatement.executeUpdate();
    
            ResultSet generatedKeys = componenteStatement.getGeneratedKeys();
            int codiceComponente = -1;
            if (generatedKeys.next()) {
                codiceComponente = generatedKeys.getInt(1);
            }
            return codiceComponente;
        }
    }
    
    private static void inserisciAssemblaggio(Connection connection, int numeroGara, int codiceComponente) throws SQLException { //inserisce vettura e i suoi componenti nel databsse
        try (PreparedStatement assemblaggioStatement = connection.prepareStatement("INSERT INTO Assemblaggio (Vettura, Componente, Data_Installazione) VALUES (?, ?, NOW())")) {
            assemblaggioStatement.setInt(1, numeroGara);
            assemblaggioStatement.setInt(2, codiceComponente);
            assemblaggioStatement.executeUpdate();
        }
    }
    

    //OPERAZIONE 3
    private static void aggiuntaPilotaAdEquipaggio(int idPilota, String nomePilota, String cognomePilota, int numeroEquipaggio) throws SQLException {
        // RV3: Verifica Tipo Pilota Gentlemen
        if (verificaTipoPilotaGentleman(nomePilota)) { 
            try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Pilota (Codice_Pilota, Nome, Cognome) VALUES (?, ?, ?)")) {
                preparedStatement.setInt(1, idPilota);
                preparedStatement.setString(2, nomePilota);
                preparedStatement.setString(3, cognomePilota);
                preparedStatement.executeUpdate();
    
                // Inserimento nella tabella Composizione
                try (PreparedStatement composizioneStatement = connection.prepareStatement("INSERT INTO Composizione (Pilota, Equipaggio) VALUES (?, ?)")) {
                    composizioneStatement.setInt(1, idPilota);
                    composizioneStatement.setInt(2, numeroEquipaggio);
                    composizioneStatement.executeUpdate();
                    System.out.println("Pilota aggiunto all'equipaggio con successo.");
                }
            }
        } else {
            System.out.println("Un equipaggio non può essere composto da soli gentleman driver.");  //implementazione del requisito non funzionale RV3
        }
    }
    

    private static boolean verificaTipoPilotaGentleman(String nomePilota) throws SQLException { ///serve per controllare se un pilota è un gentleman driver, utile per il requisito non funzionale RV3
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT tp.Tipo FROM Pilota p JOIN TipologiaPilota tp ON p.Descrizione_ID = tp.ID WHERE p.Nome = ?")) {
            preparedStatement.setString(1, nomePilota);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String tipoPilota = resultSet.getString("Tipo");
                    return "Gentleman".equals(tipoPilota);
                }
            }
        }
        return false;  // Ritorna false nel caso il pilota non esista nel database o non abbia un tipo associato
    }
    
    

    //OPERAZIONE 4
    private static void registrazioneFinanziamento(String nomePilota, String nomeScuderia, int codiceFinanziamento, double quota) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Finanziamento (Pilota, Scuderia, Codice_Finanziamento, Quota) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, nomePilota);
            preparedStatement.setString(2, nomeScuderia);
            preparedStatement.setInt(3, codiceFinanziamento);
            preparedStatement.setDouble(4, quota);
            preparedStatement.executeUpdate();
            System.out.println("Finanziamento registrato con successo.");
        }
    }

    //OPERAZIONE 5
    private static void iscrizioneVetturaAGara(int numeroGara, int numeroVettura) throws SQLException {
        if (verificaPartecipazioneGara(numeroGara, numeroVettura)) {
            try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Guida (Equipaggio, Vettura) VALUES (?, ?)")) {
                preparedStatement.setInt(1, numeroGara);
                preparedStatement.setInt(2, numeroVettura);
                preparedStatement.executeUpdate();
                System.out.println("Vettura iscritta alla gara con successo.");
            }
        } else {
            System.out.println("La stessa vettura non può partecipare più di una volta alla stessa gara"); //implementazione del reauisito non funzionale RV2
        }
    }

    private static boolean verificaPartecipazioneGara(int numeroGara, int numeroVettura) throws SQLException { //metodo che controlla se una vettura è già iscritta ad ua gara (utilizzando il numeorgara), utile per il requisito non funzionale RV2
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS NumPartecipazioni " + "FROM Guida " + "WHERE Vettura = ? AND Gara = ?")) {
            preparedStatement.setInt(1, numeroVettura);
            preparedStatement.setInt(2, numeroGara);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int numPartecipazioni = resultSet.getInt("NumPartecipazioni");
                    return numPartecipazioni == 0; // Restituisce true se la vettura non è già iscritta alla gara
                }
            }
        }
        return false;
    }
    

    //OPERAZIONE 6
    private static void registrazioneRisultatoGara(int numeroGara, int numeroVettura, int Punti_Guadagnati, String motivoRitiro) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Partecipazione SET Punti_Guadagnati = ?, Motivo_Ritiro = ? " + "WHERE Gara = ? AND Vettura = ?")) {
            preparedStatement.setInt(1, Punti_Guadagnati);
            preparedStatement.setString(2, motivoRitiro);
            preparedStatement.setInt(3, numeroGara);
            preparedStatement.setInt(4, numeroVettura);
            preparedStatement.executeUpdate();
            System.out.println("Risultato registrato con successo.");
        }
    }

    //OPERAZIONE 7 
    private static boolean verificaMontaggioComponente(int numeroGara, int codiceComponente) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS NumMontaggi " + "FROM Assemblaggio " + "WHERE Vettura = ? AND Componente = ?")) {
            preparedStatement.setInt(1, numeroGara);
            preparedStatement.setInt(2, codiceComponente);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int numMontaggi = resultSet.getInt("NumMontaggi");
                    return numMontaggi == 0; // Restituisce true se il componente non è ancora montato
                }
            }
        }
        return false;
    }

    //OPERAZIONE 8
    private static void stampaTotaleFinanziamentiPerScuderia() throws SQLException {
        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
            String query = "SELECT Scuderia.Nome, SUM(Finanziamento.Quota) AS TotaleFinanziamenti " + "FROM Scuderia LEFT JOIN Finanziamento ON Scuderia.Nome = Finanziamento.Scuderia " +"GROUP BY Scuderia.Nome";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String nomeScuderia = resultSet.getString("Nome");
                    double totaleFinanziamenti = resultSet.getDouble("TotaleFinanziamenti");
                    System.out.println("Scuderia: " + nomeScuderia + ", Totale Finanziamenti: " + totaleFinanziamenti);
                }
            }
        }
    }

    //OPERAZIONE 9
    private static void stampaScuderiePartecipantiConFinanziamentiAnnuale(int anno) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT Scuderia.Nome, COUNT(Finanziamento.Codice_Finanziamento) AS NumFinanziamenti " + "FROM Scuderia LEFT JOIN Finanziamento ON Scuderia.Nome = Finanziamento.Scuderia " + "WHERE YEAR(Finanziamento.Data_Transazione) = ? " + "GROUP BY Scuderia.Nome")) {
            preparedStatement.setInt(1, anno);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String nomeScuderia = resultSet.getString("Nome");
                    int numFinanziamenti = resultSet.getInt("NumFinanziamenti");
                    System.out.println("Scuderia: " + nomeScuderia + ", Num. Finanziamenti: " + numFinanziamenti);
                }
            }
        }
    }

    //OPERAZIONE 10
    private static void visualizzaVincitoriCircuitoDiCasa(String nazionalitaPilota, String paeseCircuito) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Pilota.Nome, Pilota.Cognome " + "FROM Pilota INNER JOIN Equipaggio ON Pilota.Nome = Equipaggio.Pilota " + "INNER JOIN Guida ON Equipaggio.Numero_Equipaggio = Guida.Equipaggio " + "INNER JOIN Gara ON Guida.Vettura = Gara.Nome_Circuito " + "WHERE Pilota.Nazionalita = ? AND Gara.Paese = ?")) {
            preparedStatement.setString(1, nazionalitaPilota);
            preparedStatement.setString(2, paeseCircuito);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String nomePilota = resultSet.getString("Nome");
                    String cognomePilota = resultSet.getString("Cognome");
                    System.out.println("Pilota vincitore nel circuito di casa: " + nomePilota + " " + cognomePilota);
                }
            }
        }
    }

    //OPERAZIONE 11 
    private static void visualizzaPercentualeGentlemanDriverPerScuderia() throws SQLException {
        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
            String query = "SELECT Scuderia.Nome, COUNT(CASE WHEN Tipologia_Pilota.Tipo = 'Gentleman Driver' THEN 1 END) " + "/ COUNT(*) * 100 AS PercentualeGentlemanDriver " + "FROM Scuderia LEFT JOIN Equipaggio ON Scuderia.Nome = Equipaggio.Scuderia " + "LEFT JOIN Composizione ON Equipaggio.Numero_Equipaggio = Composizione.Equipaggio " + "LEFT JOIN Tipologia_Pilota ON Composizione.Pilota = Tipologia_Pilota.Pilota " + "GROUP BY Scuderia.Nome";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String nomeScuderia = resultSet.getString("Nome");
                    double percentualeGentlemanDriver = resultSet.getDouble("PercentualeGentlemanDriver");
                    System.out.println("Scuderia: " + nomeScuderia + ", Percentuale Gentleman Driver: " + percentualeGentlemanDriver + "%");
                }
            }
        }
    }
    
    //OPERAZIONE 12
    private static void stampaCostruttoriMensile() throws SQLException {
        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
            String query = "SELECT Costruttore.Nome, COUNT(Componente.Codice) AS NumComponenti " +"FROM Costruttore LEFT JOIN Componente ON Costruttore.Nome = Componente.Nome_Costruttore " +"GROUP BY Costruttore.Nome";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String nomeCostruttore = resultSet.getString("Nome");
                    int numComponenti = resultSet.getInt("NumComponenti");
                    System.out.println("Costruttore: " + nomeCostruttore +", Num. Componenti Forniti: " + numComponenti);
                }
            }
        }
    }

    //OPERAZIONE 13
    private static void stampaClassificaFinale() throws SQLException {
        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
            String query = "SELECT Vettura.Numero_Gara, SUM(Partecipazione.Punti_Guadagnati) AS PuntiTotali " +"FROM Vettura LEFT JOIN Partecipazione ON Vettura.Numero_Gara = Partecipazione.Vettura " +"GROUP BY Vettura.Numero_Gara ORDER BY PuntiTotali DESC";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    int numeroGara = resultSet.getInt("Numero_Gara");
                    int puntiTotali = resultSet.getInt("PuntiTotali");
                    System.out.println("Gara: " + numeroGara + ", Punti Totali: " + puntiTotali);
                }
            }
        }
    }

    //OPERAZIONE 14 
    private static void stampaClassificheFinaliPerTipoMotore() throws SQLException {
        String[] tipiMotore = {"Turbo", "Aspirato"};
        for (String tipoMotore : tipiMotore) {
            try (Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT Vettura.Numero_Gara, SUM(Partecipazione.Punti_Guadagnati) AS PuntiTotali " +"FROM Vettura LEFT JOIN Partecipazione ON Vettura.Numero_Gara = Partecipazione.Vettura " + "LEFT JOIN Componente ON Vettura.Numero_Gara = Componente.Codice " + "LEFT JOIN Motore ON Componente.Codice = Motore.Componente " + "WHERE Motore.Tipo = ? " + "GROUP BY Vettura.Numero_Gara ORDER BY PuntiTotali DESC")) {
                preparedStatement.setString(1, tipoMotore);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int numeroGara = resultSet.getInt("Numero_Gara");
                        int puntiTotali = resultSet.getInt("PuntiTotali");
                        System.out.println("Gara: " + numeroGara +", Punti Totali (" + tipoMotore + "): " + puntiTotali);
                    }
                }
            }
        }
    }

    //OPERAZIONE 15
    private static void stampaRapportoPuntiMinutiScuderia() throws SQLException {
        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
            String query = "SELECT Scuderia.Nome, AVG(Partecipazione.Punti_Guadagnati / Gara.Durata) AS RapportoPuntiMinuti " + "FROM Scuderia LEFT JOIN Equipaggio ON Scuderia.Nome = Equipaggio.Scuderia " + "LEFT JOIN Guida ON Equipaggio.Numero_Equipaggio = Guida.Equipaggio " + "LEFT JOIN Vettura ON Guida.Vettura = Vettura.Numero_Gara " + "LEFT JOIN Partecipazione ON Vettura.Numero_Gara = Partecipazione.Vettura " + "LEFT JOIN Gara ON Partecipazione.Gara = Gara.Nome " + "GROUP BY Scuderia.Nome";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String nomeScuderia = resultSet.getString("Nome");
                    double rapportoPuntiMinuti = resultSet.getDouble("RapportoPuntiMinuti");
                    System.out.println("Scuderia: " + nomeScuderia + ", Rapporto Punti/Minuti: " + rapportoPuntiMinuti);
                
            }
        }
    }
}

//METODO MAIN
public static void main(String[] args) {
    try {
        // Operazione 1 : Registrazione di una scuderia
        registrazioneScuderia("Ferrari", "Italia");

        // Operazione 2 : Inserimento dei dati di un'autovettura con componenti
        inserimentoVettura("SF90", "Ferrari", "Turbo", "Carbonio", 8);

        // Operazione 3 : Aggiunta di un nuovo pilota ad un equipaggio
        aggiuntaPilotaAdEquipaggio("NomePilota", "CognomePilota", "EquipaggioX");

        // Operazione 4 : Registrazione di un finanziamento per una scuderia
        registrazioneFinanziamento("PilotaX", "ScuderiaY", 123, 50000.0);

        // Operazione 5 : Iscrizione di una vettura ad una gara
        iscrizioneVetturaAGara("VetturaX", "GaraZ");

        // Operazione 6 : Registrazione del risultato conseguito da ciascuna vettura iscritta ad una gara
        registrazioneRisultatoGara("VetturaX", "GaraZ", 1, "Terminata");

        // Operazione 7 : Verifica delle possibilità di montare un componente su una vettura 
        boolean verificaMontaggio = verficaMontaggioComponente(1,1);

        // Operazione 8 : Per ciascuna scuderia, stampare la somma totale dei finanziamenti ricevuti 
        stampaTotaleFinanziamentiPerScuderia();

        // Operazione 9 : Stampa annuale delle scuderia che hanno partecipato al campionato, compreso il numero di finanziamenti
        stampaScuderiePartecipantiConFinanziamentiAnnuale(2024);

        // Operazione 10 : Visualizzare i piloti che hanno vinto nel <<circuto di casa>> (es. pilota italiano che vince in un circuito italiano)
        visualizzaVincitoriCircuitoDiCasa("Italiana", "Monza");

        // Operazione 11 : Per ciascuina scuderia, visualizzare la percentulae di gentleman driver di cui si compone l'equipaggio
        visualizzaPercentualeGentlemanDriverPerScuderia();

        // Operazione 12 : Stampa mensile dei costruttori compreso il numero di componenti che ha fornito
        stampaCostruttoriMensile();

        // Operazione 13 : Stampa della classifica finale dei punti conseguiti da tutte le vetture
        stampaClassificaFinale();

        // Operazione 14 : Stampa delle classifica finali di puyni per tipo di motore
        stampaClassificheFinaliPerTipoMotore();

        // Operazione 15 : Stampare un  report che elenchi ciasucna scuderia sulla base del rapporto dei punti/minuti di gara, mediando tra le macchien appartenenti a ciascuna scuderia
        stampaRapportoPuntiMinutiScuderia();

    } catch (SQLException e) {
        System.err.println("Errore durante l'esecuzione di un'operazione sul database: " + e.getMessage()); //eccezione con stampa di un errore 
    }
}
}