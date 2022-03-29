# DIL - Laboratoire de méthodologie

## Auteurs : Silvestri Géraud, Marengo Stéphane, Rosset Loïc, Tissot-Daguette Olivier

# Objectifs

L'objectif principal de ce projet est de prendre en main les différents outils utilisés lors de développement en groupe.
Il permet également de se familiariser à la méthodologie agile.

Chaque modification doit s'effectuer en passant par la création d'une `branch`, puis d'une `pull request` devant ensuite
être approuvée par un autre membre du groupe avant d'être acceptée et incorporée au projet.

De plus, le projet contient une [license](./LICENSE) protégeant la propriété et un [*code of
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

- `new`
- `build`
- `serve`
- `clean`
