# openflexo-jdbc — Backlog

Features and evolutions to develop in the JDBC technology adapter (JDBC connections and the Hibernate-based mapping). Each entry says
what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `JDBC-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### JDBC-F-1 — The JDBC adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-jdbc#5](https://github.com/openflexo-team/openflexo-jdbc/issues/5)

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`): the edition actions of the JDBC adapter caught the failures of
their evaluations, logged them and went on with null; a missing table, connection or name gave a warning and null; `AddJDBCLine` declared
no exception. `HbnException`, raised by every Hibernate failure, was an untyped `FMLExecutionException`, and the unchecked exceptions of
Hibernate (a query that does not parse, a persistence failure) became internal errors. A failing SQL query behind `select X from db` was a
warning, the select returning the cached instances. The creation scheme of `CreateHbnObject` never ran (`CORE-D-73`).

**Done (decided with the user, 2026-11-16).** Scope: the paths that FML execution goes through.
- `HbnException` extends `FMLTechnologyError`: every failure of Hibernate is a typed technology error. The unchecked exceptions of
  Hibernate met while FML runs (`PersistenceException`, `IllegalArgumentException`, `IllegalStateException`, in a query, a transaction, a
  save, the read of an object) are raised as `HbnException` (`HbnException.isHibernateFailure`).
- Edition actions (`AddJDBCLine`, `AddJDBCTable`, `CreateJDBCConnection`, `OpenTransaction`, `CommitTransaction`, `RollbackTransaction`,
  `SaveHbnObject`, `RefreshHbnObject`, `CreateHbnObject`) evaluate through `FMLEvaluation` and declare `throws FMLExecutionException`. A null
  table, connection, name, virtual model instance, object or container is an `FMLNullReferenceError`; an unknown column an
  `FMLResolutionError`; a line without any value an `FMLTechnologyError`; an object that is not a Hibernate object an `FMLTypeError`. A typed
  failure of `AbstractCreateResource` is rethrown unchanged (it was wrapped again into an untyped error).
- Fetch requests (`SelectJDBCTable`, `SelectJDBCLine`, `SelectJDBCColumn` and their unique forms, `PerformSQLQuery`): a null connection,
  table or virtual model instance is an `FMLNullReferenceError` naming the select; a failing query an `HbnException`.
- `HbnVirtualModelInstance.getFlexoConceptInstances(concept)`, the query behind `select X from db`: a failing query raises its
  `HbnException` through `FMLFailureTunnel`, and the concept is not marked as queried (the next read queries again); the read of one object
  (`getFlexoConceptInstance(identifier)`) likewise. A user interface reading it gets the unchecked tunnel.
- The factory of instances (`HbnVirtualModelInstanceModelFactory`) declares no exception: it runs no creation scheme and cannot fail, so the
  printed catches of `HbnVirtualModelInstance` are gone.
- `CreateHbnObject`: its creation scheme now runs on the new instance (`AbstractAddFlexoConceptInstance`, `CORE-D-73`): a failing scheme
  fails the creation and leaves no instance.
- The role reading of `HbnFlexoConceptInstance` keeps no strict override: `getFlexoActor` maps the columns and references
  (`getFlexoActorOrFail` defaults to it).
- After review: `HbnException.isHibernateFailure` keeps a `PersistenceException`, and an `IllegalArgumentException` / `IllegalStateException`
  only when thrown by Hibernate or the persistence API (its first frame, or a persistence cause): the same exceptions thrown by OpenFlexo
  code stay defects. `RefreshHbnObject` raises a failing refresh as an `HbnException`. An `HbnException` built from an exception without
  message takes its text.
- Known limits (found by the review, not changed):
  - a creation whose scheme fails is discarded from the model (`performCoreDeletion`), not from the Hibernate session: a `SaveHbnObject` run
    by the scheme before it failed leaves the row in the session, inserted at the next commit;
  - a one-to-many reference is a lazy Hibernate bag: reading it from FML can run SQL, whose failure is not wrapped (an internal error);
  - a value of `AddJDBCLine` whose column name or evaluated value is null is still skipped silently;
  - `RefreshHbnObject` does not evaluate its receiver (it was unused).

**Left as they are (scope).** The `printStackTrace` / `System.out` of connection, mapping and type resolution code (`HbnVirtualModelInstance`
loading of its connection and mapping validation, `HbnVirtualModelInstanceType`, the commented-out code of `HbnToOneReferenceRole`).

**Tests.** `TestHbnAnnotatedMapping` (jdbc-ta-test, in-memory HSQL database) gains: a `CreateHbnObject` whose creation scheme fails (the
behaviour fails with an `FMLNullReferenceError`, no instance is left), a `CreateHbnObject` whose creation scheme sets a column (it runs on
the new instance), a failing query behind `getFlexoConceptInstances` (an `HbnException`, typed, through the tunnel: a query on a concept
given a fake mapping, a semantic error of Hibernate; a database failure is not tested, nor the select of FML unwrapping the tunnel, read
in `FMLEvaluation.convert`). `CreateHbnObject`
has no FML name: it is built in Java, no `.fmlscript` can reach it.

**Verified (2026-11-16).** Green through the openflexo-dev composite: jdbc-ta-test 68 (37 run, 31 legacy tests ignored), formod (b-ta-test,
formod-test), openflexo-integration-tests (4 projects). ECJ compiles jdbc-ta. Reviewed by `openflexo-reviewer` (its findings fixed or
recorded here). Not checked in the running application (a select on a stopped database, a refresh of an unsaved object).
