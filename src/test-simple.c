#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "defines.h"
#include "protein.h"
#include "topology.h"
#include "pdbqtio.h"
#include "pdbatom.h"

int main(int argc, char *argv[]) {
    pdb_atom_t *atoms = Malloc(pdb_atom_t, 1024);
    load_pdbqt_file(atoms, 0, argv[1]);
    return EXIT_SUCCESS;
}