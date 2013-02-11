    ----------------------------------------------
               Atelier de Maintenance
    Jate 13.2.11 - (c) philippe.billerot@gmail.com
    ----------------------------------------------

    1 / PRESENTATION

    2 / LANCEMENT DE L'ATELIER
    3 / PARAMETRAGE DE L'ATELIER
        3.1 / Param�trer l'atelier
        3.2 / Param�trer les outils
        3.3 / Param�trer les actions
        3.4 / Param�trer les extensions
    4 / LES COMMANDES DE L'ATELIER
        4.1 / COMMANDES DOS
        4.2 / COMMANDES SQL
    5 / LA FENETRE RESULTAT
    6 / MACROS DANS LES LIGNES DE COMMANDES
    7 / HISTORIQUE DES MISES A JOUR

1 / PRESENTATION
    ------------
    Jate est atelier pour maintenir un ensemble de fichiers texte.
    Les noms de fichier sont pr�sent�s dans une liste (la partie gauche de l'atelier)
    Le contenu de la liste est modifiable � souhait.

    Les fichiers texte sont visualisables et modifiables dans l'�diteur de l'atelier
    (la partie droite de l'atelier).
    Il est possible d'ouvrir plusieurs fichiers.

    La partie inf�rieure est un fen�tre o� seront affich�s les r�sultats des actions
    r�alis�es dans l'�diteur ou des commandes du menu Outil.

     -----------------------------------------
    | liste       | �diteur                   |
    |             |                           |
    |             |                           |
    |             |                           |
     -----------------------------------------
    | r�sultat                                |
    |                                         |
     -----------------------------------------

2 / LANCEMENT DE L'ATELIER
    ----------------------

    L'atelier a �t� d�velopp� en java avec jdk 1.3 de SUN.
    Le runtime java 1.2 ou version suivante devra donc �tre install� pr�alablement.

    java.exe -jar jate.jar [fichier de configuration]
    ou
    javaw.exe -jar jate.jar [fichier de configuration]

    [fichier de configuration]
        param�tre optionnel pour pr�ciser le nom du fichier de configuration
        o� seront m�moris�es les propri�t�s de l'atelier :
        - taille, position de la fen�tre
        - commandes du menu outil
        - actions possibles dans le menu contextuel de l'�diteur
        par d�faut le nom du fichier de configuration est jate.ate

    Le r�pertoire de travail de l'atelier est celui donn� au d�marrage.
    Par exemple pour cr�er un raccourci sur le bureau :

        Cible :
            u:\repertoireDuJar\jate.jar
        D�marrer en :
            x:\repertoireAilleurs\jate.ate

3 / PARAMETRAGE DE L'ATELIER
    ------------------------
    Le menu Fichier propose 3 possibilit�s pour personnaliser l'atelier :

    param�trer l'atelier...
        pour personnaliser les options g�n�rales de l'atelier

    param�trer les outils...
        pour sp�cifier les commandes que l'on veut mettre � disposition dans le menu Outils

    param�trer les actions...
        pour sp�cifier les commandes qui seront propos�es dans le menu contextuel de l'�diteur

    param�trer les extensions...
        pour associer une application � l'ouverture du fichier. Dans ce cas, le fichier ne
        sera pas ouvert dans l'�diteur de l'atelier.

    3.1 / param�trer l'atelier
          --------------------
        aide.cmd=
            la commande dos pour afficher l'aide en ligne qui sera accessible dans le menu "?"
            {$edit;readme.txt}

        aide.titre=Aide
            le texte qui sera affich�e dans la ligne du menu d'aide

        atelier.liste=jate.lst
            le nom du fichier texte qui m�morisera la liste des fichiers g�r�s dans l'atelier
            par d�faut : jate.lst

        atelier.titre=Atelier de Maintenance
            le titre qui sera affich� dans la barre de titre de la fen�tre de l'atelier

        ftp.open=
            adresse ip ou nom DNS du serveur FTP (propos� par d�faut � la connexion)
            D�s lors que ce param�tre est renseign� l'�diteur disposera des fonctions ftp
        ftp.user=
            nom du logon sur le site FTP (propos� par d�faut � la connexion)
            (le mot de passe sera demand� � la connexion)

    3.2 / param�trer les outils
          ---------------------

        Le titre du menu tout d'abord sera sp�cifi� dans la variable :

        outils.titre = Outils

        Une ligne du menu sera d�finie dans 2 variables :

        outils.X.cmd=
            la commande qui sera lanc�e
        outils.X.titre=
            le titre de la commande dans le menu

            X d�marrera � 1.

        exemple :

        outils.titre = Outils
        outils.1.cmd=dos javadoc -version -author -d javadoc -public ja*.java
        outils.1.titre=G�n�rer la doc Javadoc
        outils.2.cmd=dos createJar.bat
        outils.2.titre=Cr�er le fichier jar
        outils.3.cmd=dos {commande dos}
        outils.3.titre=Commande DOS
        outils.4.cmd=dos compilerAll.bat
        outils.4.titre=Compiler tout

    3.3 / param�trer les actions
          ----------------------
        m�me principe que pour les outils :

        actions.1.cmd=sql %1 {param�tre SQL}
        actions.1.titre=ex�cuter SQL
        actions.2.cmd=dos %1
        actions.2.titre=ex�cuter DOS
        actions.3.cmd=dos javac {$fichier} -verbose -deprecation
            -classpath .;d\:\\jsdk\\lib\\jsdk2.jar
        actions.3.titre=Java Compiler...

    3.4 / param�trer les extensions
          -------------------------
          
        extensions.txt=notepad %1
        extensions.ini=notepad %1
        extensions.bmp=pbrush %1

        extension.exe=nul ou null
            pour sp�cifier � l'atelier de ne rien lancer lorsque le fichier
            aura l'extension indiqu�e.

4 / LES COMMANDES DE L'ATELIER
    --------------------------

    2 types de commandes :
    - des commandes DOS
    - des commandes SQL

    Le r�sulat des commandes sera affich� dans la fen�tre r�sultat.

    4.1 / COMMANDES DOS
          -------------
          Une commande DOS sera pr�fix�e par dos ...
          suivie de la commande qui sera propos�e au shell du syst�me.

          Exemple :
          dos dir c:\temp

    4.2 / COMMANDES SQL
          -------------
          Une commande SQL sera pr�fix�e par sql ...
          suivie de la commande qui sera au moteur du gestionnaire de la base de donn�e.

          Exemple :
          sql select * from tableCLIENTS

5 / LA FENETRE RESULTAT
    -------------------

    La fen�tre inf�rieure de l'atelier affiche le r�sultat des commande DOS ou SQL.

    Elle permet aussi la saisie de commandes (dos ou sql) directement dedans.
    La validation se fera alors par la touche F5 sur la ligne de commande.

6 / MACROS DANS LES LIGNES DE COMMANDES
    -----------------------------------

    Une ligne de commande pourra comprendre une macro qui sera interpr�t�e avant par
    l'atelier :
    
    {nom param�tre}
        le param�tre sera demand� avant l'envoi de la commande
        la valeur saisie remplacera la macro dans la commande
        
    {nom param�tre, item 1, item 2, ...}
        le param�tre sera pr�sent� cette fois � travers une liste
        L'item choisi remplacera la macro dans la commande
        Si l'item est de la forme "(code) label", c'est le code seulement qui sera retourn�e.
        
    {$fichier}
        sera remplac�e par le nom du fichier ouvert dans l'�diteur

    {$repertoireAtelier}
        sera remplac�e par le r�pertoire de l'atelier

    {$ecouteSocket;n� du port}
        l'atelier se met � l'�coute sur un socket avec le n� du port donn�.

    {$socket;adresse du serveur;n� du port;message}
        envoie le message sur le socket distant avec l'adresse et le n� du port donn�s.

    {$mail;serveur smtp;email �metteur;email destinataire;email en copie;
        sujet;nom du fichier HTML}
        envoie un email.

    {$edit;fichier}
        ouvre le fichier dans une fen�tre d'�dition.

    {$play;file://chemin du fichier son}
        ouvre la fen�tre du player et joue le fichier son

    {$getHttp;url de la requ�te}
        lance une requ�te sur un serveur HTTP.

    LIGNES DE COMMANDE DANS L'EDITEUR
    Le contenu de la fen�tre de l'�diteur peut �tre interpr�t� puis ex�cut�e en pla�ant
    %1 derri�re le nom de l'interpr�teur :

    dos %1
        le contenu de la fen�tre de l'�diteur sera envoy�e dans l'entr�e standard du shell
        de commande
    sql %1
        le contenu de la fen�tre de l'�diteur sera envoy� dans l'entr�e standard
        du moteur SQL.
        Les commandes seront termin�es par un point virgule.

        Exemple:

        /* ceci est un commentaire
           sur plusieurs lignes    */
        CONNECT ARBRES,SA;                           // nom du driver ODBC user mdp

        DROP TABLE ARBLIEUX;
        CREATE TABLE ARBLIEUX
         (
            idLieu          varchar(9) NOT NULL,     // n� du lieux
            pays            varchar(30) NULL,
            departement     varchar(9) NULL,
            commune     varchar(30) NULL,
            CONSTRAINT cleARBLIEUX PRIMARY KEY (idLieu)
        );

        // commentaire sur une ligne
        INSERT INTO ARBLIEUX VALUES ('1', 'FRANCE', '79', 'ST MAIXENT L ECOLE');
        INSERT INTO ARBLIEUX VALUES ('2', 'FRANCE', '79', 'AIFFRES');
        INSERT INTO ARBLIEUX VALUES ('3', 'FRANCE', '79', 'NIORT');
        SELECT * FROM ARBLIEUX;
        
    dos commande %0
        le param�tre %0 sera remplac� par le nom du fichier en �dition
        
    dos commande %2 
        le param�tre %2 sera remplac� par le contenu de la ligne courante de l'�diteur

7 / HISTORIQUE DES MISES A JOUR
    ---------------------------
version 3.0 du 25 d�cembre 2011
- adaptation lde l'atelier au monde linux (debian)
- en particulier l'acc�s au fichier commen�ant par /

version 1.90 du 10 novembre 2005
- bug gestion des param�tres avec JRE 1.4
- impression pdf sans description du chemin d'acc�s du reader

version 1.80 du 8 juillet 2004
- utilisation des api de mail.jar

version 1.70 du 22 mai 2003
- correction traitement des actions - bug suite traitement du %2

version 1.69 du 6 mai 2003
- correction d�tection changement du param�trage des outils

version 1.68 du 9 avril 2003
- modif de la macro $mail 
  body = nom d'un fichier
- correction dialog des outils

version 1.67 du 18 f�vrier 2003
- ajout du param�tre %0 et %2 dans la ligne de commande

version 1.66 du 3 f�vrier 2003
- correction suppression d'un fichier dans la liste de gauche

version 1.64 du 2 janvier 2003
- correction lecture des outils si liste vide

version 1.61 de octobre 2002
- ajout param�tre de type liste {param,item 1,item2,...}
- nouvelle dialog de saisie des outils et actions
- modif technique du projet d�velopp� d�sormais dans Eclipse.
  Les version pr�c�dentes �taient d�velopp� dans JBuilder 4 qui ne permettait plus
  d'�voluer sauf en prenant une version payante.

version 1.60 de mars 2002
- ajout gestion des extensions
- version 1.51 du 1er d�cembre 2001
- ajout user et mot de passe � la commande SQL connect
- connect nomBase user motDePasse;
- suppression param�tre print.cmd print.titre

version 1.5 de octobre 2001
- ajout fonctions FTP sur l'�diteur avec soumission de job sur le MVS

version 1.3 de juin 2001
- ajout de la macro $play
- ajout de la macro $getHttp

version 1.1 de f�vrier 2001
- ajout de la macro $ecouteSocket
- ajout de la macro $socket
- ajout de la macro $mail
- ajout de la macro $edit

novembre 2000
- la premi�re version 1.0

    Vos remarques et suggestions seront les bienvenues.

                                                       philippe.billerot@laposte.net
