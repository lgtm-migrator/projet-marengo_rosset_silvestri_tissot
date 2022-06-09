# DIL - Laboratoire de méthodologie

## Auteurs : Silvestri Géraud, Marengo Stéphane, Rosset Loïc, Tissot-Daguette Olivier

# Objectifs

L'objectif principal de ce projet est de prendre en main les différents outils utilisés lors de développement en groupe.
Il permet également de se familiariser à la méthodologie agile.

Chaque modification doit s'effectuer en passant par la création d'une `branch`, puis d'une `pull request` devant ensuite
être approuvée par un autre membre du groupe avant d'être acceptée et incorporée au projet.

De plus, le projet contient une [licence](./LICENSE) protégeant la propriété et un [*code of
conduct*](./CODE_OF_CONDUCT.md) décrivant les règles morales à suivre pour garder une bonne entente de groupe.

Suite à un problème technique chez un des membres du groupe, il n'a pas été possible de forcer la signature des commits
avec une clé GPG.

# Description de l'application

L'application, réalisée avec [Picocli](https://picocli.info/), est un utilitaire CLI permettant de lancer 4
sous-commandes différentes.

Pour le moment, les sous-commandes se contentent d'afficher un message spécifique à chaque commande.

# Installation

Les exécutables peuvent être générés en exécutant la commande `mvn clean install` et seront disponibles dans le dossier
`target/site/bin`.

Pour que le programme soit utilisable comme une commande, il est nécessaire de l'ajouter à la variable `path`.

**Windows:** [Voir la marche à suivre](https://java.com/fr/download/help/path.html)

**Unix:** ```export PATH=$PATH:`pwd\`/site/bin```

L'application peut désormais être lancée en utilisant la commande `site`.

# Utilisation

Lors du lancement de l'application, il est nécessaire de spécifier la sous-commande désirée parmi la liste suivante :

- `init`
    - Prends en paramètre le chemin relatif où l'on souhaite créer le site
    - Crée le dossier spécifié s'il n'existe pas. Ajoute un fichier de configuration (en yaml), un template de base du
      site (en markdown) et un dossier "template" contenant le layout (en html) de base.
    - Exemple : site init /monSite


- `build`
    - Prends en paramètre le chemin relatif d'un dossier contenant des fichiers markdown
    - Crée un dossier `build` qui contiendra la représentation HTML des fichiers markdown contenu dans le dossier et les
      éventuelles images. Les fichiers de configurations (.yaml, .yml, etc.) ne seront pas placés dans le
      dossier `build` du site. L'arborescence du dossier sera reproduite dans le dossier `build`
    - Les fichiers html créés contiendront le layout et le template spécifié
    - L'option `--watch` permet au `build` de s'effectuer automatiquement dès lors qu'il y a un changement dans les
      fichiers
    - Exemple : site build /mon/site


- `serve`
    - Prends en paramètre le chemin relatif d'un site
    - Permet de lancer un serveur WEB afin de visualiser le site spécifié depuis un navigateur, il est nécessaire de
      faire la compilation du site au préalable
    - L'option `--watch` permet au `serve` de faire automatiquement une publish dès lors qu'il y a un changement dans
      les fichiers
    - Exemple : site serve /mon/site


- `clean`
    - Prends en paramètre le chemin relatif d'un site
    - Permet de supprimer le dossier `build` du site spécifié et son contenu
    - Exemple : site clean /mon/site
    -

- `version`
    - Permet d'afficher la version du logiciel `site`
    - Exemple : site -V, --version

- `help`
    - Permet d'afficher les commandes disponibles du logiciel et une brève description
    - Exemple : site -h, --help