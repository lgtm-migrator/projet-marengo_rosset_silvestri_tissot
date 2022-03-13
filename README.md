# DIL - Laboratoire de méthodologie

## Auteurs : Silvestri Géraud, Marengo Stéphane, Rosset Loïc, Tissot-Daguette Olivier

# Objectifs
L'objectif principal de ce projet est de prendre en main les différents outils utilisés lors de développement en groupe. Il permet également de se familiariser à la méthodologie agile.

Chaque modification doit s'effectuer en passant par la création d'une `branch`, puis d'une `pull request` devant ensuite être approuvée par un autre membre du groupe avant d'être acceptée et incorporée au projet.

De plus, le projet contient une [license](./LICENSE) protégeant la propriété et un [*code of conduct*](./CODE_OF_CONDUCT.md) décrivant les règles morales à suivre pour garder une bonne entente de groupe.

Suite à un problème technique chez un des membres du groupe, il n'a pas été possible de forcer la signature des commits avec une clé GPG.

# Description de l'application
L'application, réalisée avec [Picocli](https://picocli.info/), est un utilitaire CLI permettant de lancer 4 sous-commandes différentes.

Pour le moment, les sous-commandes se contentent d'afficher un message spécifique à chaque commande.

# Utilisation
Lors du lancement de l'application, il est nécessaire de spécifier la sous-commande désirée parmi la liste suivante : 
- `new`
- `build`
- `serve`
- `clean`

Après compilation et génération d'un `jar` dans le dossier `target` avec Maven (`mvn package`), il suffit d'exécuter `java -jar Main-1.0.jar <sous-commande>` pour exécuter l'application.
